package jbenchmarker.rgaSplitRDSL;

import Tools.MyLogger;
import crdt.PreconditionException;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.factories.RGAFactory;
import jbenchmarker.factories.RgaRDSLFactory;
import jbenchmarker.factories.RgaSplitRDSLFactory;
import jbenchmarker.rga.RGAMerge;
import jbenchmarker.rgaRDSL.RgaRDSLMerge;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RgaSplitRDSLMergeTest {
    private static final int REPLICA_ID = 7;
    private RgaSplitRDSLMerge replica;

    @Before
    public void setUp() throws Exception {
        replica = (RgaSplitRDSLMerge) new RgaSplitRDSLFactory().create(REPLICA_ID);
    }

    @Test
    public void testEmptyTree() {
        assertEquals("", replica.lookup());
    }

    @Test
    public void testEndInsert() throws PreconditionException {
        replica.applyLocal(SequenceOperation.insert(0, "a"));
        replica.applyLocal(SequenceOperation.insert(1, "b"));
        replica.applyLocal(SequenceOperation.insert(2, "c"));
        replica.applyLocal(SequenceOperation.insert(3, "d"));
        replica.applyLocal(SequenceOperation.insert(4, "e"));
        replica.applyLocal(SequenceOperation.insert(5, "f"));
        replica.applyLocal(SequenceOperation.insert(6, "g"));
        assertEquals("abcdefg", replica.lookup());
        replica.print();
    }
    @Test
    public void testEndInsert26() throws PreconditionException {
        for(int i = 0; i < 26; i++) {
            String s = "" + (char) ('a' + i);
            replica.applyLocal(SequenceOperation.insert(i, s));
        }
        assertEquals("abcdefghijklmnopqrstuvwxyz", replica.lookup());
        replica.print();
    }

    @Test
    public void frontInsert() throws PreconditionException {
        replica.applyLocal(SequenceOperation.insert(0, "a"));
        replica.applyLocal(SequenceOperation.insert(0, "b"));
        replica.applyLocal(SequenceOperation.insert(0, "c"));
        replica.applyLocal(SequenceOperation.insert(0, "d"));
        replica.applyLocal(SequenceOperation.insert(0, "e"));
        replica.applyLocal(SequenceOperation.insert(0, "f"));
        replica.applyLocal(SequenceOperation.insert(0, "g"));
        assertEquals("gfedcba", replica.lookup());
        replica.print();
    }

    @Test
    public void randomInsert() throws PreconditionException {
        replica.applyLocal(SequenceOperation.insert(0, "a"));
        replica.applyLocal(SequenceOperation.insert(1, "b"));
        replica.applyLocal(SequenceOperation.insert(2, "c"));
        replica.applyLocal(SequenceOperation.insert(3, "d"));
        replica.applyLocal(SequenceOperation.insert(1, "x"));
        replica.applyLocal(SequenceOperation.insert(0, "y"));
        assertEquals("yaxbcd", replica.lookup());
    }

    @Test
    public void randomInsert100() throws PreconditionException {
        RGAMerge replicaRGA = (RGAMerge) new RGAFactory().create(REPLICA_ID);
        for(int i = 0; i < 100; i++) {
            int pos = (int) (Math.random() * (i + 1));
            String s = "" + (char) ('a' + pos % 26);
            MyLogger.log(String.format("@@@@ insert %s at pos %d", s, pos));
            replica.applyLocal(SequenceOperation.insert(pos, s));
            replicaRGA.applyLocal(SequenceOperation.insert(pos, s));
            replica.print();
            assertEquals(replicaRGA.lookup(), replica.lookup());
        }
        assertEquals(replicaRGA.lookup(), replica.lookup());
    }

    @Test
    public void testBlockEndInsert() throws PreconditionException {
        replica.applyLocal(SequenceOperation.insert(0, "aaa"));
        replica.applyLocal(SequenceOperation.insert(3, "bb"));
        replica.applyLocal(SequenceOperation.insert(5, "cccc"));
        replica.print();
        assertEquals("aaabbcccc", replica.lookup());
    }

    @Test
    public void testBlockFrontInsert() throws PreconditionException {
        replica.applyLocal(SequenceOperation.insert(0, "aaa"));
        replica.applyLocal(SequenceOperation.insert(0, "bb"));
        replica.applyLocal(SequenceOperation.insert(0, "cccc"));
        replica.print();
        assertEquals("ccccbbaaa", replica.lookup());
    }

    @Test
    public void randomBlockInsert100() throws PreconditionException {
        RGAMerge replicaRGA = (RGAMerge) new RGAFactory().create(REPLICA_ID);
        for(int i = 0; i < 100; i++) {
            int pos = (int) (Math.random() * (i + 1));
            String s = ("" + (char) ('a' + pos % 26)).repeat(pos % 5 + 1);
            MyLogger.log(String.format("@@@@ insert %s at pos %d", s, pos));
            replica.applyLocal(SequenceOperation.insert(pos, s));
            replicaRGA.applyLocal(SequenceOperation.insert(pos, s));
            replica.print();
            assertEquals(replicaRGA.lookup(), replica.lookup());
        }
    }

    @Test
    public void testInsert() throws PreconditionException {
        replica.applyLocal(SequenceOperation.insert(0, "abcdejk"));
        replica.print();
        replica.applyLocal(SequenceOperation.insert(5, "fghi"));
        assertEquals("abcdefghijk", replica.lookup());
        replica.print();
    }
}
