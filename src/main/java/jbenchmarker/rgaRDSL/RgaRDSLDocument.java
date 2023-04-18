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
    private RDSLNode<RGANode> rdslHead = new RDSLNode<RGANode>(null, RDSLPath.MAX_LEVEL);
//    private RGANode dataHead = null
    private int numberOfRgaNodes = 0;
    private int maxLevel = 0;

    public RgaRDSLDocument() {
        super();
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
        if(level > 0) rdslNode = new RDSLNode<RGANode>(newnd, level);
        for(int l = 1; l <= rgadoc.getMaxLevel(); l++) {
            if(l <= level) {
                rdslNode.updateDistance(l, op.getPath().getDistance(l));
                rdslNode.updateRightDistance(l, newnd.getDistance(0) - rdslNode.getDistance(l));
            } else {
                updateRdslHead(l, 1);
            }
        }

    }

    public  int getMaxLevel() {
        return this.maxLevel;
    }
    public int startLevel() {
        return this.maxLevel;
//        if(this.numberOfRgaNodes == 0) return 0;
//        return (int) Math.floor(Math.log(this.numberOfRgaNodes) / Math.log(1 / p));
    }

    public void updateMaxLevel(int level) {
        this.maxLevel = this.maxLevel > level ? this.maxLevel : level;
    }

    public void updateRdslHead(int level, int delta) {
        this.rdslHead.updateRightDistance(level, delta);
    }
}
