package jbenchmarker.rgaRDSL;

import jbenchmarker.RDSL.RDSLNode;
import jbenchmarker.RDSL.RDSLPath;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.rga.RGANode;
import jbenchmarker.rga.RGAOperation;
import jbenchmarker.rga.RGAS4Vector;

public class RgaRDSLOperation<T> extends RGAOperation<T> {
    private RDSLPath path;

    RgaRDSLOperation(int pos, RGAS4Vector s4vpos, T c, RGAS4Vector s4vtms, RDSLPath<RGANode> path) {
        super(pos, s4vpos, c, s4vtms);
        this.path = path;
    }
    public RDSLPath getPath() {
        return this.path;
    }
}
