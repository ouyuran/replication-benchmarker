package jbenchmarker.rgaRDSL;

import crdt.Operation;
import crdt.simulator.IncorrectTraceException;
import jbenchmarker.RDSL.RDSLNode;
import jbenchmarker.RDSL.RDSLPath;
import jbenchmarker.core.Document;
import jbenchmarker.core.SequenceOperation;
import jbenchmarker.rga.*;

import java.util.ArrayList;
import java.util.List;

public class RgaRDSLMerge extends RGAMerge {
    public RgaRDSLMerge(Document doc, int r) {
        super(doc, r);
    }

    @Override
    protected List<Operation> localInsert(SequenceOperation opt) throws IncorrectTraceException {
        List<Operation> lop = new ArrayList<Operation>();
        RgaRDSLDocument rgadoc = (RgaRDSLDocument) (this.getDoc());
        RGAS4Vector s4vtms, s4vpos = null;
        RgaRDSLOperation rgaop;
        RGANode target = null;

        int p = opt.getPosition();
        int offset;

        offset = opt.getContent().size();
        RDSLPath<RGANode> path = new RDSLPath<>(rgadoc.startLevel());
        s4vpos = rgadoc.getVisibleS4V(p, path); // if head, s4vpos = null; if after tail, s4vpos= the last one.
        RgaRDSLOperation[] ops = new RgaRDSLOperation[offset];
        for (int i = 0; i < offset; i++) {
            this.siteVC.inc(this.getReplicaNumber());
            s4vtms = new RGAS4Vector(this.getReplicaNumber(), this.siteVC);
            rgaop = new RgaRDSLOperation(p + i, s4vpos, opt.getContent().get(i), s4vtms, path);
            s4vpos = s4vtms; // The s4v of the current insert becomes the s4vpos of next insert.
            lop.add(rgaop);
            ops[i] = rgaop;
//          rgadoc.apply(rgaop);
//			purger.setLastVC(this.getReplicaNumber(),this.siteVC);
        }

        for (int i = offset - 1; i >= 0; i--) {
            rgadoc.apply(ops[i]);
        }

        return lop;
    }

    public void print() {
        ((RgaRDSLDocument) this.getDoc()).print();
    }
}
