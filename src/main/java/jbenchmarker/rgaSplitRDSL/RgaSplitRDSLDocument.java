package jbenchmarker.rgaSplitRDSL;

import crdt.Operation;
import jbenchmarker.RDSL.RDSLNode;
import jbenchmarker.RDSL.RDSLPath;
import jbenchmarker.RDSL.RDSLWalker;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.rgasplit.RgaSDocument;
import jbenchmarker.rgasplit.RgaSNode;

public class RgaSplitRDSLDocument<T> extends RgaSDocument<T> {

    private RDSLNode<RgaSNode> rdslHead;

    public RgaSplitRDSLDocument() {
        super();
        this.rdslHead = new RDSLNode<>(this.getHead(), RDSLPath.MAX_LEVEL);
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
            //todo
            int offset = left.getDistance(0) + posLeft;
            System.out.println(String.format("#### split %s, %d; posLeft %d", left.getContentString(), left.getDistance(0), posLeft));
            right = remoteSplit(left, offset);
            this.rdslHead.handleUpdate(left, path, offset);
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
}
