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
package jbenchmarker.logootSplitRDSL;

import crdt.CRDT;
import crdt.Operation;
import crdt.simulator.IncorrectTraceException;
import jbenchmarker.RDSL.RDSLWalker;
import jbenchmarker.core.Document;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.logoot.ListIdentifier;
import jbenchmarker.logoot.LogootOperation;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mehdi urso
 */
public class LogootSplitRDSLMerge<T> extends MergeAlgorithm {

    // nbBit <= 64
    public LogootSplitRDSLMerge(Document doc, int r) {
        super(doc, r);

    }

    @Override
    public LogootSplitRDSLDocument<T> getDoc() {
        return (LogootSplitRDSLDocument<T>) super.getDoc();
    }

    @Override
    protected void integrateRemote(Operation message) {
        getDoc().apply(message);
    }

    @Override
    protected List<Operation> localDelete(SequenceOperation opt) throws IncorrectTraceException {
        return null;
        //todo
//        List<Operation> lop = new ArrayList<Operation>();
//        int offset = opt.getLenghOfADel(), position = opt.getPosition();
//
//        for (int k = 1; k <= offset; k++) {
//            LogootOperation<T> wop = LogootOperation.delete(getDoc().getId(position + k));
//            lop.add(wop);
//        }
//        getDoc().remove(position, offset);
//        return lop;
    }

    @Override
    protected List<Operation> localInsert(SequenceOperation opt) throws IncorrectTraceException {
        List<Operation> lop = new ArrayList<Operation>();
        int N = opt.getContent().size(), position = opt.getPosition();
        RDSLWalker walker = getDoc().findLeftFromPosition(position);
        LogootSplitRDSLNode left = (LogootSplitRDSLNode) walker.getPath().getLastDataNode();
        LogootSplitRDSLNode right = (LogootSplitRDSLNode) left.getRight(0);
        ListIdentifier leftId = left.getId();
        ListIdentifier rightId = right != null ? right.getId() : null;

        List<T> content = opt.getContent();
        List<ListIdentifier> patch = getDoc().generateIdentifiers(leftId, rightId, N);

        ArrayList<T> lc = new ArrayList<T>(patch.size());
//        for (int cmpt = patch.size() - 1; cmpt >= 0; cmpt--) {
//            T c = content.get(cmpt);
//            LogootOperation<T> log = LogootOperation.insert(patch.get(cmpt), c);
//            lop.add(log);
//            getDoc().insert(left, new LogootSplitRDSLNode<>(c, patch.get(cmpt)), walker.getPath());
//        }
        return lop;
    }

    // For tests
    @Override
    protected List<? extends Operation> localUpdate(SequenceOperation opt) throws IncorrectTraceException {
        return super.localUpdate(opt);
    }

    @Override
    public CRDT<String> create() {
        return new LogootSplitRDSLMerge(getDoc().create(), 0);
    }

    @Override
    public void setReplicaNumber(int replicaNumber) {
        super.setReplicaNumber(replicaNumber);
        getDoc().setReplicaNumber(replicaNumber);
    }

    public void print() {
        getDoc().print();
    }
}
