/**
 * Replication Benchmarker
 * https://github.com/score-team/replication-benchmarker/
 * Copyright (C) 2013 LORIA / Inria / SCORE Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jbenchmarker.logootSplitRDSL;

import crdt.Factory;
import crdt.Operation;
import jbenchmarker.RDSL.RDSLNode;
import jbenchmarker.RDSL.RDSLPath;
import jbenchmarker.RDSL.RDSLWalker;
import jbenchmarker.core.SequenceOperation.OpType;
import jbenchmarker.logoot.ListIdentifier;
import jbenchmarker.logoot.LogootOperation;
import jbenchmarker.logoot.LogootStrategy;
import jbenchmarker.logoot.TimestampedDocument;
import jbenchmarker.logootRDSL.LogootRDSLIdWalker;

import java.util.List;

/**
 * A Logoot document. Contains a list of Charater and the corresponding list of LogootIndentitifer.
 * @author urso mehdi
 */
public class LogootSplitRDSLDocument<T> implements  Factory<LogootSplitRDSLDocument<T>>, TimestampedDocument {
    private int myClock;
    protected int replicaNumber;

//    final protected RangeList<ListIdentifier> idTable;
//    final protected RangeList<T> document;
//    private SinglyList<T> dataItems;
    private LogootSplitRDSLNode<T> dataHead;
    private RDSLNode<LogootSplitRDSLNode<T>> rdslHead;
    private List<Character> head;
    final protected LogootStrategy strategy;

    public LogootSplitRDSLDocument(int r, LogootStrategy strategy) {
        super();
//        idTable = new RangeList<ListIdentifier>();
        this.strategy = strategy;
        this.replicaNumber = r;

        myClock = 0;
        this.dataHead = new LogootSplitRDSLNode<>(null, strategy.begin());
        this.dataHead.addAfter(new LogootSplitRDSLNode<>(null, strategy.end()));
        this.rdslHead = new RDSLNode<>(this.dataHead, RDSLPath.MAX_LEVEL);
    } 
    
    @Override
    public String view() {
        StringBuilder s = new StringBuilder();
        LogootSplitRDSLNode current = (LogootSplitRDSLNode) this.dataHead.getRight(0);
        while (!current.getId().equals(strategy.end())) {
            s.append(current.getContentString());
            current = (LogootSplitRDSLNode) current.getRight(0);
        }
        return s.toString();
    }

    @Override
    public int viewLength() {
        return view().length();
    }

    public RDSLWalker<LogootSplitRDSLNode> findLeftFromPosition(int pos) {
        int level = Math.max(this.rdslHead.getHeadLevel(), 1);
        RDSLPath<LogootSplitRDSLNode> path = new RDSLPath(level);
        RDSLWalker<LogootSplitRDSLNode> walker = new RDSLWalker<LogootSplitRDSLNode>(this.rdslHead, pos, level, path);
        while(!walker.finish()){
            if(walker.shouldGoRight()) {
                walker.goRight();
            } else {
                walker.goDown();
            }
        }
        return walker;
    }

    public RDSLWalker<LogootSplitRDSLNode> findLeftFromIdentifier(ListIdentifier id) {
        int level = Math.max(this.rdslHead.getHeadLevel(), 1);
        RDSLPath<LogootSplitRDSLNode> path = new RDSLPath(level);
        LogootRDSLIdWalker<LogootSplitRDSLNode> walker = new LogootRDSLIdWalker<LogootSplitRDSLNode>(this.rdslHead, id, level);
        while(!walker.finish()){
            if(walker.shouldGoRight()) {
                walker.goRight();
            } else {
                walker.goDown();
            }
        }
        return walker;
    }
    
    @Override
    public void apply(Operation op) {
        LogootOperation lg = (LogootOperation) op;
        ListIdentifier idToSearch = lg.getPosition();
        LogootSplitRDSLNode left = (LogootSplitRDSLNode) this.findLeftFromIdentifier(idToSearch).getPath().getLastDataNode();
        //Insertion et Delete
        if (lg.getType() == OpType.insert) {
            left.addAfter(new LogootSplitRDSLNode<>((List<T>) lg.getContent(), idToSearch));
        } else {
            left.removeAfter();
        }
    }
    
    public void insert(LogootSplitRDSLNode<T> left, LogootSplitRDSLNode<T> node, RDSLPath path) {
        left.addAfter(node);
        this.rdslHead.handleInsert(node, path);
    }
    
    public void remove(LogootSplitRDSLNode<T> left) {
        left.removeAfter();
        //todo
    }
    
    /**
     * Get the ith identifier in the table. O means begin marker.
     */
//    public ListIdentifier getId(int pos) {
//        return idTable.get(pos);
//    }

//    @Override
//    public int viewLength() {
//        return document.size()-2;
//    }

    // TODO : duplicate strategy ?
    @Override
    public LogootSplitRDSLDocument<T> create() {
        return new LogootSplitRDSLDocument<T>(replicaNumber, strategy);
    }

    @Override
    public int nextClock() {
        return this.myClock++;
    }

    void setClock(int c) {
        this.myClock = c;
    }

    public void setReplicaNumber(int replicaNumber) {
        this.replicaNumber = replicaNumber;
    }

    @Override
    public int getReplicaNumber() {
        return replicaNumber;
    }

    List<ListIdentifier> generateIdentifiers(ListIdentifier left, ListIdentifier right, int N) {
        return strategy.generateLineIdentifiers(this, left, right, N);
    }

    public void print() {
        this.rdslHead.print(2);
    }
}
