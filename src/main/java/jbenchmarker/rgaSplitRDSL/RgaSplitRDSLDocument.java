package jbenchmarker.rgaSplitRDSL;

import jbenchmarker.RDSL.RDSLNode;
import jbenchmarker.RDSL.RDSLPath;
import jbenchmarker.rga.RGANode;
import jbenchmarker.rgasplit.RgaSDocument;
import jbenchmarker.rgasplit.RgaSNode;

public class RgaSplitRDSLDocument extends RgaSDocument {

    private RDSLNode<RgaSNode> rdslHead;

    public RgaSplitRDSLDocument() {
        super();
        this.rdslHead = new RDSLNode<>(this.getHead(), RDSLPath.MAX_LEVEL);
    }

    public void print() {
        this.rdslHead.print(2);
    }

    public int startLevel() {
        return Math.max(this.rdslHead.getLevel(), 1);
    }
}
