package jbenchmarker.rgaSplitRDSL;

import Tools.MyLogger;
import crdt.Operation;
import jbenchmarker.RDSL.RDSLHeadNode;
import jbenchmarker.RDSL.RDSLNode;
import jbenchmarker.RDSL.RDSLPath;
import jbenchmarker.RDSL.RDSLWalker;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.rgasplit.RgaSDocument;
import jbenchmarker.rgasplit.RgaSNode;

import java.util.List;

public class RgaSplitRDSLDocument<T> extends RgaSDocument<T> {

    private RDSLHeadNode<RgaSNode> rdslHead;

    public RgaSplitRDSLDocument() {
        super();
        this.rdslHead = new RDSLHeadNode<>(this.getHead(), RDSLPath.MAX_LEVEL);
    }

    public void print() {
        this.rdslHead.print(5);
    }

    public int startLevel() {
        return Math.max(this.rdslHead.getHeadLevel(), 1);
    }

    public RDSLWalker<RgaSNode> walk(int p) {
        RDSLPath<RgaSNode> path = new RDSLPath(this.startLevel());
        RDSLWalker<RgaSNode> walker = new RDSLWalker(this.rdslHead, p, this.startLevel(), path);
        while(!walker.finish()){
            if(walker.shouldGoRight()) {
                walker.goRight();
            } else {
                walker.goDown();
            }
        }
        return walker;
    }

    public void apply(Operation op) {
        RgaSplitRDSLOperation rgaop = (RgaSplitRDSLOperation) op;

        if (rgaop.getType() == SequenceOperation.OpType.delete) {
            remoteDelete(rgaop);
        } else {
            localInsert(rgaop);
        }
    }

    private void localInsert(RgaSplitRDSLOperation op) {
        RgaSNode newnd = new RgaSNode(op.getS3vtms(), op.getContent());
//        RgaSNode node, next=null;
//        RgaSS3Vector s3v = op.getS3vtms();
        RDSLWalker walker = op.getWalker();
        RDSLPath path = walker.getPath();
        int posLeft = walker.getPosLeft();
        RgaSNode left = (RgaSNode) path.getLastDataNode();
        RgaSNode right = null;
        if(posLeft < 0) {
            int offset = left.getDistance(0) + posLeft;
            //MyLogger.log(String.format("#### split %s, %d; posLeft %d, offset %d", left.getContentString(), left.getDistance(0), posLeft, offset));
            right = localSplit(left, offset);
            this.rdslHead.handleUpdate(left, path, posLeft);
            this.rdslHead.handleInsert(right, path);
        } else {
            right = left.getNext();
        }

        newnd.setNext(right);
        left.setNext(newnd);
        hash.put(op.getS3vtms(), newnd);
        size+=newnd.size();
        this.rdslHead.handleInsert(newnd, path);
    }

    public RgaSNode localSplit(RgaSNode node, int offset) {
        if (node.size() > offset){

            List<T> a= null;
            List<T> b = null;

            if (node.isVisible()){
                a = node.getContent().subList(0, offset);
                b = node.getContent().subList(offset, node.size());
            }
            // only for localSplit, have not calculated offsetAbs which is needed for remote op
            RgaSNode end = new RgaSNode(node.clone(), b, 0);
            end.setNext(node.getNext());

            node.setContent(a);
            node.setNext(end);
            node.setLink(end);
            //MyLogger.log(String.format("left %s, right %s", node.getContentString(), end.getContentAsString()));
            hash.put(node.getKey(), node);
            hash.put(end.getKey(), end);
            return end;
        }
        return null;
    }
}
