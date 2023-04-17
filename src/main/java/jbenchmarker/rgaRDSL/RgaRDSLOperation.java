package jbenchmarker.rgaRDSL;

import jbenchmarker.RDSL.RDSLPath;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.rga.RGAOperation;
import jbenchmarker.rga.RGAS4Vector;

public class RgaRDSLOperation extends RGAOperation {
    private RDSLPath path;
    public RgaRDSLOperation(int pos, RGAS4Vector s4vpos, T c, RGAS4Vector s4vtms, RDSLPath path) {
        super(SequenceOperation.OpType.insert, pos, s4vpos, c, s4vtms);
        this.path = path;
    }

    public RDSLPath getPath() {
        return this.path;
    }
}
