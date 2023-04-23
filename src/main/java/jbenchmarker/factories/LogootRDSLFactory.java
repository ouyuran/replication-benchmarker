package jbenchmarker.factories;

import jbenchmarker.RDSL.RDSLWalkable;
import jbenchmarker.core.MergeAlgorithm;
import jbenchmarker.core.ReplicaFactory;
import jbenchmarker.logoot.BoundaryStrategy;
import jbenchmarker.logoot.LogootDocument;
import jbenchmarker.logoot.LogootMerge;
import jbenchmarker.logoot.LogootStrategy;
import jbenchmarker.logootRDSL.LogootRDSLDocument;
import jbenchmarker.logootRDSL.LogootRDSLMerge;
import jbenchmarker.rgaSplitRDSL.RgaSplitRDSLDocument;
import jbenchmarker.rgaSplitRDSL.RgaSplitRDSLMerge;

public class LogootRDSLFactory<T extends RDSLWalkable> extends ReplicaFactory {
    @Override
    public LogootRDSLMerge<T> create(int r) {
        return new LogootRDSLMerge<T>(createDoc(r, 64, 1000000000), r);
    }

    static public LogootRDSLDocument createDoc(int r, int base, int bound) {
        LogootStrategy s = new BoundaryStrategy(base, bound);
        return new LogootRDSLDocument(r, s);
    }
}
