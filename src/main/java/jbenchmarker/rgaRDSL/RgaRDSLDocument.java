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
    private int numberOfRgaNodes = 0;
    private int maxLevel = 0;

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
            RemoteInsert(rgaop);
        }
    }
    private void RemoteInsert(RgaRDSLOperation op) {
        RGANode newnd = new RGANode(op.getS4VTms(), op.getContent());
        RGANode prev, next;
        RGAS4Vector s4v = op.getS4VTms();
        prev = (RGANode) op.getPath().getLastDataNode();
//        if (op.getS4VPos() == null) {
//            prev = head;
//        } else {
//            prev = hash.get(op.getS4VPos());
//        }
        if (prev == null) {
            throw new NoSuchElementException("RemoteInsert");
        }
        next = prev.getNext();

        while (next != null) {
            if (s4v.compareTo(next.getKey()) == RGAS4Vector.AFTER) {
                break;
            }
            prev = next;
            next = next.getNext();
            op.getPath().addLevel0(new RDSLFootPrint(prev, 0));
        }

        newnd.setNext(next);
        prev.setNext(newnd);
        hash.put(op.getS4VTms(), newnd);
        ++size;

        // update RDSL
        int level = RDSLNode.getRandomLevel();
        RDSLNode<RGANode> rdslNode = null;
        if(level > 0) {
            rdslNode = new RDSLNode<RGANode>(newnd, level);
            this.updateMaxLevel(level);
        }
        System.out.println(String.format("# %s, level %d", newnd.getContent(), level));
        for(int l = 1; l <= this.getMaxLevel(); l++) {
            RDSLFootPrint leftFp = op.getPath().getLastFootPrintOfLevel(l);
            RDSLNode left = leftFp != null ? (RDSLNode) leftFp.getNode() : this.rdslHead;
            RDSLNode right = left.getRight(l);
            if(l <= level) {
                left.setRight(l, rdslNode);
                rdslNode.setRight(l, right);
                rdslNode.updateDistance(l, op.getPath().getDistance(l));
                System.out.println(String.format("Update self distance %s, level %d, delta %d", newnd.getContent(), l, op.getPath().getDistance(l)));
                if(right != null) {
                    right.updateDistance(l, newnd.getDistance(0) - rdslNode.getDistance(l));
                    System.out.println(String.format("Update right distance %s, level %d, delta %d",
                            ((RGANode) rdslNode.getRight(l).getDataNode()).getContent(), l, op.getPath().getDistance(l)));
                }
            } else {
                if(right != null) {
                    right.updateDistance(l , 1);
                }
            }
        }
        op.getPath().addLevel0(new RDSLFootPrint(newnd, 0));
    }

    public  int getMaxLevel() {
        return this.maxLevel;
    }
    public int startLevel() {
        return Math.max(this.maxLevel, 1);
//        if(this.numberOfRgaNodes == 0) return 0;
//        return (int) Math.floor(Math.log(this.numberOfRgaNodes) / Math.log(1 / p));
    }

    public void updateMaxLevel(int level) {
        this.maxLevel = Math.max(this.maxLevel, level);
    }

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
