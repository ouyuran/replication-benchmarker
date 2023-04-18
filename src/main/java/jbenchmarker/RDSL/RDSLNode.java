package jbenchmarker.RDSL;

import jbenchmarker.rga.RGANode;

public class RDSLNode<T extends RDSLWalkable> implements RDSLWalkable{
    private T dataNode;
    private RDSLNode[] references;
    private int[] distances;

    private static final double p = 1.0 / 4;

    public RDSLNode(T dataNode, int level) {
        this.dataNode = dataNode;
        this.references = new RDSLNode[level];
        this.distances = new int[level];
    }

    public RDSLNode getRight(int level) {
        return this.references[level - 1];
    }

    public int getDistance(int level) {
        return this.distances[level - 1];
    }

    public int getRightDistance(int level) {
        if(this.references[level - 1] == null) {
            return Integer.MAX_VALUE;
        } else {
            return this.getRight(level).getDistance(level);
        }
    }

    public void updateDistance(int level, int delta) {
        this.distances[level - 1] += delta;
    }
    public void updateRightDistance(int level, int delta) {
        getRight(level).updateDistance(level, delta);
    }

    public T getDataNode() {
        return this.dataNode;
    }

    public static int getRandomLevel() {
        int level = 0;
        while(Math.random() < p) {
            level++;
        }
        return level;
    }
}
