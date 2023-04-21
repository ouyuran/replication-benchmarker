package jbenchmarker.rgaSplitRDSL;

import jbenchmarker.RDSL.RDSLPath;
import jbenchmarker.RDSL.RDSLWalker;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.rga.RGANode;
import jbenchmarker.rga.RGAS4Vector;
import jbenchmarker.rgasplit.RgaSOperation;
import jbenchmarker.rgasplit.RgaSS3Vector;

import java.util.List;

public class RgaSplitRDSLOperation<T> extends RgaSOperation {
    private RDSLWalker walker;

    RgaSplitRDSLOperation(List<T> c, RgaSS3Vector s3vpos, RgaSS3Vector s3vtms, int off1, RDSLWalker walker) {
        super(c, s3vpos, s3vtms, off1);
        this.walker = walker;
    }
    public RDSLWalker getWalker() {
        return this.walker;
    }
}
