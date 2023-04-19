package jbenchmarker.factories;

import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.ReplicaFactory;
import jbenchmarker.rgaRDSL.RgaRDSLDocument;
import jbenchmarker.rgaRDSL.RgaRDSLMerge;

public class RgaRDSLFactory extends ReplicaFactory {
    @Override
    public MergeAlgorithm create(int r) {
        return new RgaRDSLMerge(new RgaRDSLDocument(), r);
    }

    static RgaRDSLDocument createDoc(int r, int base) {
        return new RgaRDSLDocument();
    }
}
