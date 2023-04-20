package jbenchmarker.rgaRDSL;

import crdt.Operation;
import jbenchmarker.RDSL.RDSLFootPrint;
import jbenchmarker.RDSL.RDSLNode;
import jbenchmarker.RDSL.RDSLPath;
import jbenchmarker.RDSL.RDSLWalker;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.rga.RGADocument;
import jbenchmarker.rga.RGANode;
import jbenchmarker.rga.RGAOperation;
import jbenchmarker.rga.RGAS4Vector;

import java.util.NoSuchElementException;

public class RgaRDSLDocument extends RGADocument {
    private RDSLNode<RGANode> rdslHead;

    public RgaRDSLDocument() {
        super();
        this.rdslHead = new RDSLNode<>(this.getHead(), RDSLPath.MAX_LEVEL);
    }

    public RGANode getVisibleNode(int v, RDSLPath<RGANode> path) {
        RDSLWalker<RGANode> walker = new RDSLWalker<RGANode>(this.rdslHead, v, this.startLevel(), path);
        while(!walker.finish()){
            if(walker.shouldGoRight()) {
                walker.goRight();
            } else {
                walker.goDown();
            }
        }
        return path.getLastDataNode();
    }

    public RGAS4Vector getVisibleS4V(int v, RDSLPath<RGANode> path) {
        RGANode node = getVisibleNode(v, path);
        if (node == null) {
            throw new NoSuchElementException("getVisibleS4V");
        }
        return node.getKey();
    }

    public void apply(RgaRDSLOperation op) {
        RgaRDSLOperation rgaop = (RgaRDSLOperation) op;
        if (rgaop.getType() == SequenceOperation.OpType.delete) {
//            RemoteDelete(rgaop);
        } else {
            LocalInsert(rgaop);
        }
    }
    private void LocalInsert(RgaRDSLOperation op) {
        RGANode newnd = new RGANode(op.getS4VTms(), op.getContent());
        RGANode prev, next;
        RGAS4Vector s4v = op.getS4VTms();
        prev = (RGANode) op.getPath().getLastDataNode();
        if (prev == null) {
            throw new NoSuchElementException("RemoteInsert");
        }
        next = prev.getNext();

        newnd.setNext(next);
        prev.setNext(newnd);
        hash.put(op.getS4VTms(), newnd);
        ++size;

        this.rdslHead.handleInsert(newnd, op.getPath());
    }

    public  int getMaxLevel() {
        return this.rdslHead.getLevel();
    }
    public int startLevel() {
        return Math.max(this.getMaxLevel(), 1);
//        if(this.numberOfRgaNodes == 0) return 0;
//        return (int) Math.floor(Math.log(this.numberOfRgaNodes) / Math.log(1 / p));
    }

//    public void updateMaxLevel(int level) {
//        this.maxLevel = Math.max(this.maxLevel, level);
//    }

    public void updateRdslHead(int level, int delta) {
        this.rdslHead.updateRightDistance(level, delta);
    }

    public void print() {
        this.rdslHead.print(2);
    }
//    public void print() {
//        for(int i = this.maxLevel; i > 0; i--) {
//            String s = "";
//            RDSLNode current = this.rdslHead;
//            int index = 0;
//            while(current != null) {
//                int distance = current.getDistance(i);
//                if (distance == 0 || distance == 1) {
//                    s += distance;
//                } else {
//                    s = s + "-".repeat(index < 2 ? distance : distance - 1) + distance;
//                }
//                current = current.getRight(i);
//                index++;
//            }
//            System.out.println(s);
//        }
//        String s = "";
//        RGANode current = this.getHead();
//        while(current != null) {
//            if(current.isHead()) {
//                s += "*";
//            } else {
//                s += current.getContent();
//            }
//            current = current.getRight(0);
//        }
//        System.out.println(s);
//    }
}
