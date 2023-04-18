package jbenchmarker.RDSL;

public class RDSLFootPrint<T extends RDSLWalkable> {
    private T node;
    private int level;

    public RDSLFootPrint(T node, int level) {
        this.node = node;
        this.level = level;
    }

    public int getLevel() {
        return this.level;
    }

    public int getDistance() {
        return this.node.getDistance(this.level);
    }

    public T getNode() {
        return this.node;
    }
}
