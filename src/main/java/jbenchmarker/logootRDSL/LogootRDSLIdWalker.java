package jbenchmarker.logootRDSL;

import jbenchmarker.RDSL.RDSLNode;
import jbenchmarker.RDSL.RDSLWalkable;
import jbenchmarker.RDSL.RDSLWalker;
import jbenchmarker.logoot.ListIdentifier;

public class LogootRDSLIdWalker<T extends RDSLWalkable> extends RDSLWalker {
    private ListIdentifier<T> idToFind = null;
    public LogootRDSLIdWalker(RDSLNode start, ListIdentifier<T> id, int startLevel) {
        super(start, 0, startLevel);
        this.idToFind = id;
    }

    public ListIdentifier getCurrentId() {
        if(this.currentLevel > 0) {
            return ((LogootRDSLNode) this.dataNode).getId();
        } else {
            return ((LogootRDSLNode) this.getCurrentDataNode()).getId();
        }
    }
    public boolean shouldGoRight() {
        return getCurrentId().compareTo(this.idToFind) > 0;
    }
    public boolean finish() {
        return this.currentLevel == 0 &&
                ((LogootRDSLNode) this.getPath().getLastDataNode()).getId().compareTo(idToFind) == 0;
    }
}
