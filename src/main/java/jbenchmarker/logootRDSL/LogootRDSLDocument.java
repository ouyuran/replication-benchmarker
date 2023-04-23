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
package jbenchmarker.logootRDSL;

import collect.RangeList;
import crdt.Factory;
import crdt.Operation;
import jbenchmarker.RDSL.RDSLNode;
import jbenchmarker.RDSL.RDSLPath;
import jbenchmarker.RDSL.RDSLWalkable;
import jbenchmarker.RDSL.RDSLWalker;
import jbenchmarker.core.SequenceOperation.OpType;
import jbenchmarker.logoot.ListIdentifier;
import jbenchmarker.logoot.LogootOperation;
import jbenchmarker.logoot.LogootStrategy;
import jbenchmarker.logoot.TimestampedDocument;
import jbenchmarker.logootRDSL.SinglyList.Node;
import jbenchmarker.logootRDSL.SinglyList.SinglyList;
import jbenchmarker.rga.RGANode;

import java.util.LinkedList;
import java.util.List;

/**
 * A Logoot document. Contains a list of Charater and the corresponding list of LogootIndentitifer.
 * @author urso mehdi
 */
public class LogootRDSLDocument<T> implements  Factory<LogootRDSLDocument<T>>, TimestampedDocument {
    private int myClock;
    protected int replicaNumber;

//    final protected RangeList<ListIdentifier> idTable;
//    final protected RangeList<T> document;
//    private SinglyList<T> dataItems;
    private LogootRDSLNode<T> dataHead;
    private RDSLNode<LogootRDSLNode<T>> rdslHead;
    private List<Character> head;
    final protected LogootStrategy strategy;

    public LogootRDSLDocument(int r, LogootStrategy strategy) {
        super();
//        idTable = new RangeList<ListIdentifier>();
        this.strategy = strategy;
        this.replicaNumber = r;

        myClock = 0;
        this.dataHead = new LogootRDSLNode<>(null, strategy.begin());
        this.dataHead.addAfter(new LogootRDSLNode<>(null, strategy.end()));
        this.rdslHead = new RDSLNode<>(this.dataHead, RDSLPath.MAX_LEVEL);
    } 
    
    @Override
    public String view() {
        StringBuilder s = new StringBuilder();
        LogootRDSLNode current = (LogootRDSLNode) this.dataHead.getRight(0);
        while (!current.getId().equals(strategy.end())) {
            s.append(current.getContentString());
            current = (LogootRDSLNode) current.getRight(0);
        }
        return s.toString();
    }

    @Override
    public int viewLength() {
        return view().length();
    }

    public RDSLWalker<LogootRDSLNode> findLeftFromPosition(int pos) {
        int level = Math.max(this.rdslHead.getHeadLevel(), 1);
        RDSLPath<LogootRDSLNode> path = new RDSLPath(level);
        RDSLWalker<LogootRDSLNode> walker = new RDSLWalker<LogootRDSLNode>(this.rdslHead, pos, level, path);
        while(!walker.finish()){
            if(walker.shouldGoRight()) {
                walker.goRight();
            } else {
                walker.goDown();
            }
        }
        return walker;
    }

    public RDSLWalker<LogootRDSLNode> findLeftFromIdentifier(ListIdentifier id) {
        int level = Math.max(this.rdslHead.getHeadLevel(), 1);
        RDSLPath<LogootRDSLNode> path = new RDSLPath(level);
        LogootRDSLIdWalker<LogootRDSLNode> walker = new LogootRDSLIdWalker<LogootRDSLNode>(this.rdslHead, id, level);
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
        LogootRDSLNode left = (LogootRDSLNode) this.findLeftFromIdentifier(idToSearch).getPath().getLastDataNode();
        //Insertion et Delete
        if (lg.getType() == OpType.insert) {
            left.addAfter(new LogootRDSLNode<>((T) lg.getContent(), idToSearch));
        } else {
            left.removeAfter();
        }
    }
    
    public void insert(LogootRDSLNode<T> left, LogootRDSLNode<T> node, RDSLPath path) {
        left.addAfter(node);
        this.rdslHead.handleInsert(node, path);
    }
    
    public void remove(LogootRDSLNode<T> left) {
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
    public LogootRDSLDocument<T> create() {
        return new LogootRDSLDocument<T>(replicaNumber, strategy);
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
