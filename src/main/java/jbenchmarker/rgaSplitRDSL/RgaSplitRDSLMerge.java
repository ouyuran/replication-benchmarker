package jbenchmarker.rgaSplitRDSL;

import crdt.Operation;
import crdt.simulator.IncorrectTraceException;
import jbenchmarker.RDSL.RDSLPath;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.rga.RGANode;
import jbenchmarker.rgasplit.*;

import java.util.ArrayList;
import java.util.List;

public class RgaSplitRDSLMerge extends RgaSMerge {
    public RgaSplitRDSLMerge(RgaSDocument doc, int r) {
        super(doc, r);
    }

    @Override
    protected List<? extends Operation> localInsert(SequenceOperation so) throws IncorrectTraceException {

        List<Operation> lop = new ArrayList<Operation>();
        RgaSplitRDSLDocument rgadoc = (RgaSplitRDSLDocument) (this.getDoc());
        RgaSS3Vector s3vtms, s3vpos = null;
        RgaSOperation rgaop;

        RDSLPath<RgaSNode> path = new RDSLPath<>(rgadoc.startLevel());
    //todo
        if (so.getPosition() == 0) {
            s3vpos = null;
        } else {
            s3vpos = position.node.getKey().clone();
        }

        this.siteVC.inc(this.getReplicaNumber());
        s3vtms = new RgaSS3Vector(this.getReplicaNumber(), this.siteVC, 0);
        rgaop = new RgaSOperation(so.getContent(), s3vpos, s3vtms, position.offset);
        lop.add(rgaop);
        rgadoc.apply(rgaop);

        return lop;

    }

    public void print() {
        ((RgaSplitRDSLDocument) this.getDoc()).print();
    }
}
