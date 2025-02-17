/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jbenchmarker.ot.ttf;

import collect.VectorClock;
import crdt.CRDTMessage;
import crdt.Operation;
import crdt.simulator.IncorrectTraceException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.core.SequenceOperation.OpType;
import jbenchmarker.ot.soct2.OTMessage;
import jbenchmarker.ot.ttf.MC.TTFMCMergeAlgorithm;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author mehdi
 */
public class TTFMCTest {

    @Test
    public void testSimpleMergeClean1() throws IncorrectTraceException {
        TTFMCMergeAlgorithm site0 = new TTFMCMergeAlgorithm(0);
        site0.localInsert(insert(0, "abc"));

        TTFSequenceMessage op1 = TTFSequenceMessageFrom(insert(1, "x"), 0, vc(4, 0, 0));
        TTFSequenceMessage op2 = TTFSequenceMessageFrom(insert(1, "x"), 1, vc(3, 1, 0));

        site0.integrateRemote(op1);
        assertEquals("axbc", site0.lookup());
        site0.integrateRemote(op2);
        assertEquals("axbc", site0.lookup());
    }

    @Test
    public void testSimpleMergeClean2() throws IncorrectTraceException {
        TTFMCMergeAlgorithm site0 = new TTFMCMergeAlgorithm(0);
        site0.localInsert(insert(0, "abc"));

        //site0.generateLocal(insert(0, 1, "x"));
        TTFSequenceMessage op1 = TTFSequenceMessageFrom(insert(1, "x"), 0, vc(4, 0, 0));
        TTFSequenceMessage op2 = TTFSequenceMessageFrom(insert(1, "x"), 1, vc(3, 1, 0));
        TTFSequenceMessage op3 = TTFSequenceMessageFrom(delete(1, 1), 2, vc(3, 0, 1));

        site0.integrateRemote(op1);
        assertEquals("axbc", site0.lookup());
        site0.integrateRemote(op3);
        assertEquals("axc", site0.lookup());
        site0.integrateRemote(op2);
        assertEquals("axc", site0.lookup());
    }
    
    @Test
    public void testSimpleMergeClean3() throws IncorrectTraceException {
        TTFMCMergeAlgorithm site0 = new TTFMCMergeAlgorithm(0);
        TTFMCMergeAlgorithm site1 = new TTFMCMergeAlgorithm(1);
        TTFMCMergeAlgorithm site2 = new TTFMCMergeAlgorithm(2);
        
        List<Operation> ops0 = duplicate(site0.localInsert(insert(0, "abc")));
        assertEquals("abc", site0.lookup());
        
        integrateSeqAtSite(ops0, site1);
        assertEquals("abc", site1.lookup());
        //site0.generateLocal(insert(0, 1, "x"));
        List<Operation> ops00 = duplicate(site0.localInsert(insert(1, "xyz")));
        assertEquals("axyzbc", site0.lookup());
        
        List<Operation> ops1 = duplicate(site1.localInsert(insert(1, "xyz")));
        assertEquals("axyzbc", site1.lookup());
        
        integrateSeqAtSite(ops0, site2);
        assertEquals("abc", site2.lookup());
        integrateSeqAtSite(ops00, site2);
        assertEquals("axyzbc", site2.lookup());
       // integrateSeqAtSite(ops1, site2);
       // assertEquals("axyzbc", site2.lookup());
    }
    
     @Test
    public void testPartialConcurrent() throws IncorrectTraceException {
        TTFMCMergeAlgorithm site0 = new TTFMCMergeAlgorithm(0);
        TTFMCMergeAlgorithm site1 = new TTFMCMergeAlgorithm(1);
        TTFMCMergeAlgorithm site2 = new TTFMCMergeAlgorithm(2);
        
        List<Operation> ops0 = duplicate(site0.localInsert(insert(0, "abc")));
        assertEquals("abc", site0.lookup());
        
        List<Operation> ops1 = duplicate(site1.localInsert(insert(0, "xyz")));
        assertEquals("xyz", site1.lookup());
        
        List<Operation> ops2 = duplicate(site2.localInsert(insert(0, "abc")));
        assertEquals("abc", site2.lookup());
        
        integrateSeqAtSite(ops1, site0);
        integrateSeqAtSite(ops2, site0);
        
        integrateSeqAtSite(ops0, site1);
        integrateSeqAtSite(ops2, site1);
        assertEquals(site1.lookup(), site1.lookup());
    }

    //=======================Generic test====================
    @Test
    public void testGenerateLocalInsertCharByChar() throws IncorrectTraceException {
        int siteId = 0;
        TTFMCMergeAlgorithm merger = new TTFMCMergeAlgorithm(siteId);

        List<Operation> ops = merger.localInsert(insert(0, "b"));
        assertEquals(1, ops.size());
        OTMessage<TTFOperation> opg = ((TTFSequenceMessage) ops.get(0)).getSoct2Message();
        assertEquals(SequenceOperation.OpType.insert, opg.getOperation().getType());
        assertEquals('b', opg.getOperation().getContent());
        assertEquals(0, opg.getOperation().getPosition());
        assertEquals("[<0,1>]", vcToString(opg.getClock()));
        assertEquals("b", merger.lookup());


        ops = merger.localInsert(insert(0, "a"));
        assertEquals(1, ops.size());
        opg = ((TTFSequenceMessage) ops.get(0)).getSoct2Message();
        assertEquals('a', opg.getOperation().getContent());
        assertEquals(0, opg.getOperation().getPosition());
        assertEquals("[<0,2>]", vcToString(opg.getClock()));
        assertEquals("ab", merger.lookup());

        ops = merger.localInsert(insert(2, "c"));
        assertEquals(1, ops.size());
        opg = ((TTFSequenceMessage) ops.get(0)).getSoct2Message();
        assertEquals('c', opg.getOperation().getContent());
        assertEquals("[<0,3>]", vcToString(opg.getClock()));
        assertEquals(2, opg.getOperation().getPosition());
        assertEquals("abc", merger.lookup());
    }

    @Test
    public void testGenerateLocalInsertString() throws IncorrectTraceException {
        int siteId = 0;
        TTFMCMergeAlgorithm merger = new TTFMCMergeAlgorithm(siteId);

        List<Operation> ops = merger.localInsert(insert(0, "abc"));
        assertEquals(3, ops.size());

        OTMessage<TTFOperation> opg = ((TTFSequenceMessage) ops.get(0)).getSoct2Message();
        assertEquals(OpType.insert, opg.getOperation().getType());
        assertEquals('a', opg.getOperation().getContent());
        assertEquals(0, opg.getOperation().getPosition());
        assertEquals("[<0,1>]", vcToString(opg.getClock()));

        opg = ((TTFSequenceMessage) ops.get(1)).getSoct2Message();
        assertEquals('b', opg.getOperation().getContent());
        assertEquals(1, opg.getOperation().getPosition());
        assertEquals("[<0,2>]", vcToString(opg.getClock()));

        opg = ((TTFSequenceMessage) ops.get(2)).getSoct2Message();
        assertEquals('c', opg.getOperation().getContent());
        assertEquals("[<0,3>]", vcToString(opg.getClock()));
        assertEquals(2, opg.getOperation().getPosition());

        assertEquals("abc", merger.lookup());
    }

    @Test
    public void testGenerateLocalDeleteCharByChar() throws IncorrectTraceException {
        int siteId = 0;
        TTFMCMergeAlgorithm merger = new TTFMCMergeAlgorithm(siteId);
        merger.localInsert(insert(0, "abcd"));

        // remove 'a'
        List<Operation> ops = merger.localDelete(delete(0, 1));
        assertEquals(1, ops.size());
        OTMessage<TTFOperation> opg = ((TTFSequenceMessage) ops.get(0)).getSoct2Message();
        assertEquals(OpType.delete, opg.getOperation().getType());
        assertEquals(0, opg.getOperation().getPosition());
        assertEquals("[<0,5>]", vcToString(opg.getClock()));
        assertEquals("bcd", merger.lookup());

        // remove 'd'
        ops = merger.localDelete(delete(2, 1));
        assertEquals(1, ops.size());
        opg = ((TTFSequenceMessage) ops.get(0)).getSoct2Message();
        assertEquals(OpType.delete, opg.getOperation().getType());
        assertEquals(3, opg.getOperation().getPosition());
        assertEquals("[<0,6>]", vcToString(opg.getClock()));
        assertEquals("bc", merger.lookup());

        // remove 'c'
        ops = merger.localDelete(delete(1, 1));
        assertEquals(1, ops.size());
        opg = ((TTFSequenceMessage) ops.get(0)).getSoct2Message();
        assertEquals(OpType.delete, opg.getOperation().getType());
        assertEquals(2, opg.getOperation().getPosition());
        assertEquals("[<0,7>]", vcToString(opg.getClock()));
        assertEquals("b", merger.lookup());

        // remove 'b'
        ops = merger.localDelete(delete(0, 1));
        assertEquals(1, ops.size());
        opg = ((TTFSequenceMessage) ops.get(0)).getSoct2Message();
        assertEquals(OpType.delete, opg.getOperation().getType());
        assertEquals(1, opg.getOperation().getPosition());
        assertEquals("[<0,8>]", vcToString(opg.getClock()));
        assertEquals("", merger.lookup());
    }

    @Test
    public void testGenerateLocalDeleteString() throws IncorrectTraceException {
        int siteId = 0;
        TTFMCMergeAlgorithm merger = new TTFMCMergeAlgorithm(siteId);
        merger.localInsert(insert(0, "abcd"));

        // remove "abcd"
        List<Operation> ops = merger.localDelete(delete(0, 4));

        assertEquals(4, ops.size());
        OTMessage<TTFOperation> opg = ((TTFSequenceMessage) ops.get(0)).getSoct2Message();
        assertEquals(OpType.delete, opg.getOperation().getType());
        assertEquals(0, opg.getOperation().getPosition());
        assertEquals("[<0,5>]", vcToString(opg.getClock()));

        opg = ((TTFSequenceMessage) ops.get(1)).getSoct2Message();
        assertEquals(OpType.delete, opg.getOperation().getType());
        assertEquals(1, opg.getOperation().getPosition());
        assertEquals("[<0,6>]", vcToString(opg.getClock()));

        opg = ((TTFSequenceMessage) ops.get(2)).getSoct2Message();
        assertEquals(OpType.delete, opg.getOperation().getType());
        assertEquals(2, opg.getOperation().getPosition());
        assertEquals("[<0,7>]", vcToString(opg.getClock()));

        opg = ((TTFSequenceMessage) ops.get(3)).getSoct2Message();
        assertEquals(OpType.delete, opg.getOperation().getType());
        assertEquals(3, opg.getOperation().getPosition());
        assertEquals("[<0,8>]", vcToString(opg.getClock()));

        assertEquals("", merger.lookup());
    }

    @Test
    public void testGenerateLocalDeleteStringWhichContainsDeletedChars() throws IncorrectTraceException {
        int siteId = 0;
        TTFMCMergeAlgorithm merger = new TTFMCMergeAlgorithm(siteId);
        merger.localInsert(insert(0, "abcdefg"));

        merger.localDelete(delete(2, 2));
        assertEquals("abefg", merger.lookup());

        // remove "bef"
        List<Operation> ops = merger.localDelete(delete(1, 3));

        assertEquals(3, ops.size());
        OTMessage<TTFOperation> opg = ((TTFSequenceMessage) ops.get(0)).getSoct2Message();
        assertEquals(OpType.delete, opg.getOperation().getType());
        assertEquals(1, opg.getOperation().getPosition());

        opg = ((TTFSequenceMessage) ops.get(1)).getSoct2Message();
        assertEquals(OpType.delete, opg.getOperation().getType());
        assertEquals(4, opg.getOperation().getPosition());

        opg = ((TTFSequenceMessage) ops.get(2)).getSoct2Message();
        assertEquals(OpType.delete, opg.getOperation().getType());
        assertEquals(5, opg.getOperation().getPosition());

        assertEquals("ag", merger.lookup());
    }

    @Test
    public void testVectorClockEvolution() throws IncorrectTraceException {
        int siteId = 0;
        TTFMCMergeAlgorithm merger = new TTFMCMergeAlgorithm(siteId);
        assertEquals(vc(0), merger.getClock());

        OTMessage op1 = ((TTFSequenceMessage) merger.localInsert(insert(0, "a")).get(0)).getSoct2Message();
        assertEquals(vc(1), merger.getClock());

        OTMessage op2 = ((TTFSequenceMessage) merger.localInsert(insert(1, "b")).get(0)).getSoct2Message();
        assertEquals(vc(2), merger.getClock());

        assertEquals(vc(1), op1.getClock());
        assertEquals(vc(2), op2.getClock());
    }

    @Test
    public void testTP2PuzzleAtSite0() throws IncorrectTraceException {
        TTFMCMergeAlgorithm site0 = new TTFMCMergeAlgorithm(0);
        site0.localInsert(insert(0, "abc"));

        //site0.generateLocal(insert(0, 1, "x"));
        TTFSequenceMessage op1 = TTFSequenceMessageFrom(insert(1, "x"), 0, vc(4, 0, 0));
        TTFSequenceMessage op2 = TTFSequenceMessageFrom(insert(2, "y"), 1, vc(3, 1, 0));
        TTFSequenceMessage op3 = TTFSequenceMessageFrom(delete(1, 1), 2, vc(3, 0, 1));

        site0.integrateRemote(op1);
        assertEquals("axbc", site0.lookup());
        site0.integrateRemote(op2);
        assertEquals("axbyc", site0.lookup());
        site0.integrateRemote(op3);
        assertEquals("axyc", site0.lookup());
    }

    @Test
    public void testTP2PuzzleAtSite0bis() throws IncorrectTraceException {
        TTFMCMergeAlgorithm site0 = new TTFMCMergeAlgorithm(0);
        site0.localInsert(insert(0, "abc"));

        TTFSequenceMessage op1 = TTFSequenceMessageFrom(insert(1, "x"), 0, vc(4, 0, 0));
        TTFSequenceMessage op2 = TTFSequenceMessageFrom(insert(2, "y"), 1, vc(3, 1, 0));
        TTFSequenceMessage op3 = TTFSequenceMessageFrom(delete(1, 1), 2, vc(3, 0, 1));

        site0.integrateRemote(op1);
        assertEquals("axbc", site0.lookup());
        site0.integrateRemote(op3);
        assertEquals("axc", site0.lookup());
        site0.integrateRemote(op2);
        assertEquals("axyc", site0.lookup());
    }

    @Test
    public void testTP2PuzzleAtSite1() throws IncorrectTraceException {
        TTFMCMergeAlgorithm site0 = new TTFMCMergeAlgorithm(0);
        List<Operation> ops = site0.localInsert(insert(0, "abc"));

        TTFSequenceMessage op1 = TTFSequenceMessageFrom(insert(1, "x"), 0, vc(4, 0, 0));
        TTFSequenceMessage op2 = TTFSequenceMessageFrom(insert(2, "y"), 1, vc(3, 1, 0));
        TTFSequenceMessage op3 = TTFSequenceMessageFrom(delete(1, 1), 2, vc(3, 0, 1));

        TTFMCMergeAlgorithm site1 = new TTFMCMergeAlgorithm(1);
        for (Operation op : ops) {
            site1.integrateRemote(op);
        }
        site1.integrateRemote(op2);
        assertEquals("abyc", site1.lookup());
        site1.integrateRemote(op1);
        assertEquals("axbyc", site1.lookup());
        site1.integrateRemote(op3);
        assertEquals("axyc", site1.lookup());
    }

    @Test
    public void testTP2PuzzleAtSite1bis() throws IncorrectTraceException {
        TTFMCMergeAlgorithm site0 = new TTFMCMergeAlgorithm(0);
        List<Operation> ops = site0.localInsert(insert(0, "abc"));

        TTFSequenceMessage op1 = TTFSequenceMessageFrom(insert(1, "x"), 0, vc(4, 0, 0));
        TTFSequenceMessage op2 = TTFSequenceMessageFrom(insert(2, "y"), 1, vc(3, 1, 0));
        TTFSequenceMessage op3 = TTFSequenceMessageFrom(delete(1, 1), 2, vc(3, 0, 1));

        TTFMCMergeAlgorithm site1 = new TTFMCMergeAlgorithm(1);
        for (Operation op : ops) {
            site1.integrateRemote(op);
        }
        site1.integrateRemote(op2);
        assertEquals("abyc", site1.lookup());
        site1.integrateRemote(op3);
        assertEquals("ayc", site1.lookup());
        site1.integrateRemote(op1);
        assertEquals("axyc", site1.lookup());
    }

    @Test
    public void testTP2PuzzleAtSite2() throws IncorrectTraceException {
        TTFMCMergeAlgorithm site0 = new TTFMCMergeAlgorithm(0);
        List<Operation> ops = site0.localInsert(insert(0, "abc"));

        TTFSequenceMessage op1 = TTFSequenceMessageFrom(insert(1, "x"), 0, vc(4, 0, 0));
        TTFSequenceMessage op2 = TTFSequenceMessageFrom(insert(2, "y"), 1, vc(3, 1, 0));
        TTFSequenceMessage op3 = TTFSequenceMessageFrom(delete(1, 1), 2, vc(3, 0, 1));

        TTFMCMergeAlgorithm site2 = new TTFMCMergeAlgorithm(2);
        for (Operation op : ops) {
            site2.integrateRemote(op);
        }
        site2.integrateRemote(op3);
        assertEquals("ac", site2.lookup());
        site2.integrateRemote(op1);
        assertEquals("axc", site2.lookup());
        site2.integrateRemote(op2);
        assertEquals("axyc", site2.lookup());
    }

    @Test
    public void testTP2PuzzleAtSite2bis() throws IncorrectTraceException {
        TTFMCMergeAlgorithm site0 = new TTFMCMergeAlgorithm(0);
        List<Operation> ops = site0.localInsert(insert(0, "abc"));

        TTFSequenceMessage op1 = TTFSequenceMessageFrom(insert(1, "x"), 0, vc(4, 0, 0));
        TTFSequenceMessage op2 = TTFSequenceMessageFrom(insert(2, "y"), 1, vc(3, 1, 0));
        TTFSequenceMessage op3 = TTFSequenceMessageFrom(delete(1, 1), 2, vc(3, 0, 1));

        TTFMCMergeAlgorithm site2 = new TTFMCMergeAlgorithm(2);
        for (Operation op : ops) {
            site2.integrateRemote(op);
        }
        site2.integrateRemote(op3);
        assertEquals("ac", site2.lookup());
        site2.integrateRemote(op2);
        assertEquals("ayc", site2.lookup());
        site2.integrateRemote(op1);
        assertEquals("axyc", site2.lookup());
    }

    @Test
    public void testTP2Puzzle() throws IncorrectTraceException {
        TTFMCMergeAlgorithm site1 = new TTFMCMergeAlgorithm(1);
        TTFMCMergeAlgorithm site2 = new TTFMCMergeAlgorithm(2);
        TTFMCMergeAlgorithm site3 = new TTFMCMergeAlgorithm(3);

        List<Operation> ops0 = duplicate(site1.localInsert(insert(0, "abc")));
        assertEquals("abc", site1.lookup());
        integrateSeqAtSite(ops0, site2);
        assertEquals("abc", site2.lookup());
        integrateSeqAtSite(ops0, site3);
        assertEquals("abc", site3.lookup());
        List<Operation> ops1 = duplicate(site1.localInsert(insert(1, "x")));
        assertEquals("axbc", site1.lookup());
        List<Operation> ops2 = duplicate(site2.localInsert(insert(2, "y")));
        assertEquals("abyc", site2.lookup());
        List<Operation> ops3 = duplicate(site3.localDelete(delete(1, 1)));
        assertEquals("ac", site3.lookup());

        integrateSeqAtSite(ops2, site1);
        assertEquals("axbyc", site1.lookup());
        integrateSeqAtSite(ops3, site1);
        assertEquals("axyc", site1.lookup());

        integrateSeqAtSite(ops3, site2);
        assertEquals("ayc", site2.lookup());
        integrateSeqAtSite(ops1, site2);
        assertEquals("axyc", site2.lookup());

        integrateSeqAtSite(ops2, site3);
        integrateSeqAtSite(ops1, site3);
        assertEquals("axyc", site3.lookup());
    }

    @Test
    public void testBasicScenario() throws IncorrectTraceException {
        TTFMCMergeAlgorithm site0 = new TTFMCMergeAlgorithm(0);
        TTFMCMergeAlgorithm site2 = new TTFMCMergeAlgorithm(2);
        TTFMCMergeAlgorithm site4 = new TTFMCMergeAlgorithm(4);

        List<Operation> ops0 = duplicate(site0.localInsert(insert(0, "ABC")));
        assertEquals("ABC", site0.lookup());

        integrateSeqAtSite(ops0, site2);
        assertEquals("ABC", site2.lookup());

        List<Operation> ops2 = duplicate(site2.localInsert(insert(2, "vw")));
        assertEquals("ABvwC", site2.lookup());
        List<Operation> ops2b = duplicate(site2.localInsert(insert(4, "xyz")));
        assertEquals("ABvwxyzC", site2.lookup());

        integrateSeqAtSite(ops0, site4);
        assertEquals("ABC", site4.lookup());

        List<Operation> ops4 = duplicate(site4.localDelete(delete(1, 2)));
        assertEquals("A", site4.lookup());

        integrateSeqAtSite(ops4, site2);
        assertEquals("Avwxyz", site2.lookup());

        integrateSeqAtSite(ops2, site4);
        integrateSeqAtSite(ops2b, site4);
        assertEquals("Avwxyz", site4.lookup());

        assertEquals("ABC", site0.lookup()); //
        integrateSeqAtSite(ops2, site0);
        assertEquals("ABvwC", site0.lookup()); //
        integrateSeqAtSite(ops2b, site0);
        assertEquals("ABvwxyzC", site0.lookup());

        integrateSeqAtSite(ops4, site0);
        assertEquals("Avwxyz", site0.lookup());
    }

    @Test
    public void testBasicScenario2() throws IncorrectTraceException {
        TTFMCMergeAlgorithm site0 = new TTFMCMergeAlgorithm(0);
        TTFMCMergeAlgorithm site2 = new TTFMCMergeAlgorithm(2);
        TTFMCMergeAlgorithm site4 = new TTFMCMergeAlgorithm(4);

        /*
         * Ins('Salut Monsieur \nFin', 0, 1297672411625, 1, 0, [<0,1>])
         * Ins('Bonjour', 14, 1297672411625, 1, 2 [<2,1><0,1>]) Ins(' Mehdi',
         * 21, 1297672411625, 1, 2, [<2,2><0,1>]) Del(14, 3, 1297672512653, 1,
         * 4, [<0,1><4,1>])
         */
        List<Operation> ops0 = duplicate(site0.localInsert(insert(0, "Salut Monsieur  \nFin"))); // [<0,1>]        

        integrateSeqAtSite(ops0, site2);
        List<Operation> ops2 = site2.localInsert(insert(14, "Bonjour")); // [<2,1><0,1>]        
        List<Operation> ops2b = site2.localInsert(insert(21, " Mehdi")); // [<2,2><0,1>]
        assertEquals("Salut MonsieurBonjour Mehdi  \nFin", site2.lookup());

        integrateSeqAtSite(ops0, site4);
        List<Operation> ops4 = duplicate(site4.localDelete(delete(14, 3))); // [<0,1><4,1>]

        integrateSeqAtSite(ops4, site2);
        assertEquals("Salut MonsieurBonjour MehdiFin", site2.lookup());

        integrateSeqAtSite(ops2, site4);
        integrateSeqAtSite(ops2b, site4);
        assertEquals("Salut MonsieurBonjour MehdiFin", site4.lookup());

        integrateSeqAtSite(ops2, site0);
        integrateSeqAtSite(ops2b, site0);
        integrateSeqAtSite(ops4, site0);
        assertEquals("Salut MonsieurBonjour MehdiFin", site0.lookup());
    }

    @Test
    public void testPartialConcurrencyScenarioWith3Insert() throws IncorrectTraceException {
        TTFMCMergeAlgorithm site1 = new TTFMCMergeAlgorithm(1);
        TTFMCMergeAlgorithm site2 = new TTFMCMergeAlgorithm(2);

        List<Operation> ops0 = duplicate(site1.localInsert(insert(0, "ABC")));
        integrateSeqAtSite(ops0, site2);

        List<Operation> ops1 = duplicate(site1.localInsert(insert( 2, "X")));
        assertEquals("ABXC", site1.lookup());

        List<Operation> ops2 = duplicate(site2.localInsert(insert( 1, "12")));
        List<Operation> ops2b = duplicate(site2.localInsert(insert( 3, "34")));
        assertEquals("A1234BC", site2.lookup());

        integrateSeqAtSite(ops1, site2);
        assertEquals("A1234BXC", site2.lookup());

        integrateSeqAtSite(ops2, site1);
        assertEquals("A12BXC", site1.lookup());
        integrateSeqAtSite(ops2b, site1);
        assertEquals("A1234BXC", site1.lookup());
    }
    
    
     @Test
    public void testPartialConcurrencyScenarioWithDelInsert() throws IncorrectTraceException {
        TTFMCMergeAlgorithm site1 = new TTFMCMergeAlgorithm(1);
        TTFMCMergeAlgorithm site2 = new TTFMCMergeAlgorithm(2);

        List<Operation> ops0 = duplicate(site1.localInsert(insert(0, "ABC")));
        integrateSeqAtSite(ops0, site2);

        List<Operation> ops2 = duplicate(site2.localInsert(insert(1, "X")));
        assertEquals("AXBC", site2.lookup());
        List<Operation> ops22 = duplicate(site2.localInsert(insert(4, "Y")));
        assertEquals("AXBCY", site2.lookup());
        
        List<Operation> ops1 = duplicate(site1.localDelete(delete(1, 1)));
        assertEquals("AC", site1.lookup());

        integrateSeqAtSite(ops2, site1);
        assertEquals("AXC", site1.lookup());
        
        integrateSeqAtSite(ops22, site1);
        assertEquals("AXCY", site1.lookup());
        
        integrateSeqAtSite(ops1, site2);
        assertEquals("AXCY", site2.lookup());
    }
    
     
     @Test
    public void testConcurrencyScenarioWithDelInsert() throws IncorrectTraceException {
        TTFMCMergeAlgorithm site1 = new TTFMCMergeAlgorithm(1);
        TTFMCMergeAlgorithm site2 = new TTFMCMergeAlgorithm(2);

        List<Operation> ops0 = duplicate(site1.localInsert(insert(0, "ABC")));
        integrateSeqAtSite(ops0, site2);

        List<Operation> ops2 = duplicate(site2.localInsert(insert(2, "X")));
        assertEquals("ABXC", site2.lookup());
        List<Operation> ops22 = duplicate(site2.localDelete(delete(2, 1)));
        assertEquals("ABC", site2.lookup());
        
        List<Operation> ops1 = duplicate(site1.localDelete(delete(0, 1)));
        assertEquals("BC", site1.lookup());

        integrateSeqAtSite(ops2, site1);
        assertEquals("BXC", site1.lookup());
        
        integrateSeqAtSite(ops22, site1);
        assertEquals("BC", site1.lookup());
        
        integrateSeqAtSite(ops1, site2);
        assertEquals("BC", site2.lookup());
    }


    @Test
    public void testTP() throws Exception {
        TTFMCMergeAlgorithm site1 = new TTFMCMergeAlgorithm(1);
        TTFMCMergeAlgorithm site2 = new TTFMCMergeAlgorithm(2);
        TTFMCMergeAlgorithm site3 = new TTFMCMergeAlgorithm(3);
        //TTFMCMergeAlgorithm site4 = new TTFMCMergeAlgorithm(4);
        CRDTMessage mess1 = site1.insert(0, "a").clone();
        CRDTMessage mess2 = site1.remove(0, 1).clone();
        CRDTMessage mess3 = site2.insert(0, "b").clone();
        /*
         * site3.applyRemote(mess1); site3.applyRemote(mess2);
         site3.applyRemote(mess3);
         */
        site1.applyRemote(mess3.clone());

        site3.applyRemote(mess1.clone());

        site3.applyRemote(mess3.clone());

        site3.applyRemote(mess2.clone());
        assertEquals(site1.lookup(), site3.lookup());


    }
    //===========================Helpers=======================================

    private static SequenceOperation insert(int p, String s) {
        return SequenceOperation.insert(p, s);
    }

    private static SequenceOperation delete(int p, int o) {
        return SequenceOperation.delete(p, o);
    }

    private static VectorClock vc(int... v) {
        VectorClock vc = new VectorClock();
        for (int i = 0; i < v.length; i++) {
            vc.put(i, v[i]);
        }
        return vc;
    }

    private static String vcToString(VectorClock vc) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Iterator<Map.Entry<Integer, Integer>> it = vc.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Integer> e = it.next();
            sb.append("<");
            sb.append(e.getKey());
            sb.append(",");
            sb.append(e.getValue());
            sb.append(">");
        }
        sb.append("]");
        return sb.toString();
    }

    public static TTFSequenceMessage TTFSequenceMessageFrom(SequenceOperation opt, int rep, VectorClock vc) {
        TTFOperation op;
        if (opt.getType() == SequenceOperation.OpType.insert) {
            op = new TTFOperation(opt.getType(), opt.getPosition(), opt.getContent().get(0));
        } else {
            op = new TTFOperation(opt.getType(), opt.getPosition(), null);
        }
        OTMessage smess = new OTMessage(vc, rep, op);
        TTFSequenceMessage mess = new TTFSequenceMessage(smess);
        return mess;
    }

    public static List<Operation> duplicate(List<Operation> list) {
        ArrayList<Operation> res = new ArrayList<Operation>();
        for (Operation elt : list) {
            res.add(((TTFSequenceMessage) elt).clone());
        }
        return res;
    }

    private static void integrateSeqAtSite(List<Operation> seq, TTFMCMergeAlgorithm site) throws IncorrectTraceException {
        for (Operation op : duplicate(seq)) {
            site.integrateRemote(op);
        }
    }
}
