package jbenchmarker.RDSL;

public class RDSLFootPrint {
    private RDSLNode node;
    private int level;

    public RDSLFootPrint(RDSLNode node, int level) {
        this.node = node;
        this.level = level;
    }

    public int getLevel() {
        return this.level;
    }

    public int getDistance() {
        return this.node.getDistance(this.level);
    }
}
