package jbenchmarker.factories;

import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.ReplicaFactory;
import jbenchmarker.rgaRDSL.RgaRDSLDocument;
import jbenchmarker.rgaRDSL.RgaRDSLMerge;
import jbenchmarker.rgaSplitRDSL.RgaSplitRDSLDocument;
import jbenchmarker.rgaSplitRDSL.RgaSplitRDSLMerge;

public class RgaSplitRDSLFactory extends ReplicaFactory {
    @Override
    public MergeAlgorithm create(int r) {
        return new RgaSplitRDSLMerge(new RgaSplitRDSLDocument<>(), r);
    }

    static RgaSplitRDSLDocument createDoc(int r, int base) {
        return new RgaSplitRDSLDocument();
    }
}
