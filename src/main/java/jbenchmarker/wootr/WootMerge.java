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
package jbenchmarker.wootr;

import crdt.CRDT;
import crdt.CRDTMessage;
import crdt.Operation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jbenchmarker.core.Document;
import jbenchmarker.core.MergeAlgorithm;
import crdt.Operation;
import crdt.simulator.IncorrectTraceException;
import jbenchmarker.core.SequenceOperation;

/**
 *
 * @author urso
 */
public class WootMerge extends MergeAlgorithm {

    public WootMerge(Document doc, int r) {
        super(doc, r);
    }

    @Override
    protected void integrateRemote(crdt.Operation message) {
//        WootROperation wop = (WootROperation) op;
//        WootRDocument<? extends WootRNode> wdoc = (WootRDocument<? extends WootRNode>) (this.getDoc());
//        if (wop.getType()==SequenceOperation.OpType.ins && (!wdoc.has(wop.getIp()) || !wdoc.has(wop.getIp())))
//            pending.put(wop.getId(),wop);
        getDoc().apply(message);
    }

    @Override
    public CRDT<String> create() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private CRDT<String> testP() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    CRDT<String> testPP() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected List<Operation> localInsert(SequenceOperation opt) throws IncorrectTraceException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected List<Operation> localDelete(SequenceOperation opt) throws IncorrectTraceException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected List<? extends Operation> localUpdate(SequenceOperation opt) throws IncorrectTraceException {
        return localReplace(opt);
    }
}
