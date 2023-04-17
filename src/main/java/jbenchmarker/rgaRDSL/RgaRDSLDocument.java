package jbenchmarker.rgaRDSL;

import jbenchmarker.RDSL.RDSLNode;
import jbenchmarker.RDSL.RDSLPath;
import jbenchmarker.RDSL.RDSLWalker;
import jbenchmarker.rga.RGADocument;
import jbenchmarker.rga.RGANode;

public class RgaRDSLDocument extends RGADocument {
    private RDSLNode<RGANode> rdslHead = new RDSLNode<RGANode>(null, RDSLPath.MAX_LEVEL);
    @Override
    public RGANode getVisibleNode(int v) {
        RDSLWalker<RGANode> walker = new RDSLWalker<RGANode>(this.rdslHead, v, this.startLevel());
        while(!walker.finish()){
            if(walker.shouldGoRight()) {
                walker.goRight();
            } else {
                walker.goDown();
            }
        }
        RGANode current = walker.getCurrentDataNode();
        int posLeft = walker.getPosLeft();
    }

    private int startLevel() {
        //todo
        return log(this.size());
    }
}
