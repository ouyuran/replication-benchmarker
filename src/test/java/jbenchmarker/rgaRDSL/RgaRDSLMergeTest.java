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

import Tools.MyLogger;
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
    public void testInsert() throws PreconditionException {
        replica.applyLocal(SequenceOperation.insert(0, "abcdejk"));
        replica.print();
        replica.applyLocal(SequenceOperation.insert(5, "fghi"));
        assertEquals("abcdefghijk", replica.lookup());
        replica.print();
    }
}
