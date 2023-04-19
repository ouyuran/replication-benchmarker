/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/ Copyright (C) 2013
 * LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package jbenchmarker.rgaRDSL;

import crdt.Factory;
import crdt.PreconditionException;
import crdt.simulator.IncorrectTraceException;
import crdt.simulator.random.StandardSeqOpProfile;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.factories.RGAFactory;
import jbenchmarker.factories.RgaRDSLFactory;
import jbenchmarker.rga.RGAMerge;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class RgaRDSLMergeTest {

    private static final int REPLICA_ID = 7;
    private RgaRDSLMerge replica;

    @Before
    public void setUp() throws Exception {
        replica = (RgaRDSLMerge) new RgaRDSLFactory().create(REPLICA_ID);
    }

    @Test
    public void testEmptyTree() {
        assertEquals("", replica.lookup());
    }

    @Test
    public void testEndInsert() throws PreconditionException {
        System.out.println("##################");
        System.out.println("a");
        System.out.println("##################");
        replica.applyLocal(SequenceOperation.insert(0, "a"));
        System.out.println("##################");
        System.out.println("b");
        System.out.println("##################");
        replica.applyLocal(SequenceOperation.insert(1, "b"));
        System.out.println("##################");
        System.out.println("c");
        System.out.println("##################");
        replica.applyLocal(SequenceOperation.insert(2, "c"));
        System.out.println("##################");
        System.out.println("d");
        System.out.println("##################");
        replica.applyLocal(SequenceOperation.insert(3, "d"));
        System.out.println("##################");
        System.out.println("e");
        System.out.println("##################");
        replica.applyLocal(SequenceOperation.insert(4, "e"));
        System.out.println("##################");
        System.out.println("f");
        System.out.println("##################");
        replica.applyLocal(SequenceOperation.insert(5, "f"));
        System.out.println("##################");
        System.out.println("g");
        System.out.println("##################");
        replica.applyLocal(SequenceOperation.insert(6, "g"));
        assertEquals(replica.lookup(), "abcdefg");
    }

    @Test
    public void testInsert() throws PreconditionException {
        String content = "abcdejk", c2 = "fghi";
        int pos = 3;
        replica.applyLocal(SequenceOperation.insert(0, content));
        assertEquals(content, replica.lookup());
        replica.applyLocal(SequenceOperation.insert(pos, c2));
        assertEquals(content.substring(0, pos) + c2 + content.substring(pos), replica.lookup());
    }

    @Test
    public void testDelete() throws PreconditionException {
        String content = "abcdefghijk";
        int pos = 3, off = 4;
        replica.applyLocal(SequenceOperation.insert(0, content));
        assertEquals(content, replica.lookup());
        replica.applyLocal(SequenceOperation.delete(pos, off));
        assertEquals(content.substring(0, pos) + content.substring(pos + off), replica.lookup());
    }

    @Test
    public void testUpdate() throws PreconditionException {
        String content = "abcdefghijk", upd = "xy";
        int pos = 3, off = 5;
        replica.applyLocal(SequenceOperation.insert(0, content));
        assertEquals(content, replica.lookup());
        replica.applyLocal(SequenceOperation.replace(pos, off, upd));
        assertEquals(content.substring(0, pos) + upd + content.substring(pos + off), replica.lookup());
    }

    @Test
    public void testRun() throws IncorrectTraceException, PreconditionException, IOException {
        crdt.simulator.CausalDispatcherSetsAndTreesTest.testRun((Factory) new RGAFactory(), 400, 400, StandardSeqOpProfile.BASIC);
    }
}
