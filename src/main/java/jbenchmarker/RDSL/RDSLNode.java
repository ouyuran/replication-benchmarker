package jbenchmarker.RDSL;

import jbenchmarker.rga.RGANode;

public class RDSLNode<T> {
    private T dataNode;
    private RDSLNode[] references;
    private int[] distances;

    public RDSLNode(T dataNode, int level) {
        this.dataNode = dataNode;
        this.references = new RDSLNode[level];
        this.distances = new int[level];
    }

    public RDSLNode getRight(int level) {
        return this.references[level];
    }

    public int getDistance(int level) {
        return this.distances[level];
    }

    public int getRightDistance(int level) {
        if(this.references[level] == null) {
            return Integer.MAX_VALUE;
        } else {
            return this.references[level].getDistance(level);
        }
    }

    public T getDataNode() {
        return this.dataNode;
    }
}
