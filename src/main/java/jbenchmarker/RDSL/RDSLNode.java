package jbenchmarker.RDSL;

import Tools.MyLogger;

import java.util.ArrayList;

public class RDSLNode<T extends RDSLWalkable> implements RDSLWalkable{
    protected T dataNode;
    protected RDSLNode[] references;
    protected int[] distances;

    protected static final double p = 1.0 / 16;

    protected static final double[] pArray = {
            p,
            Math.pow(p, 2),
            Math.pow(p, 3),
            Math.pow(p, 4),
            Math.pow(p, 5),
            Math.pow(p, 6),
            Math.pow(p, 7),
            Math.pow(p, 8),
            Math.pow(p, 9),
            Math.pow(p, 10),
            Math.pow(p, 11),
            Math.pow(p, 12),
            Math.pow(p, 13),
            Math.pow(p, 14),
            Math.pow(p, 15),
            Math.pow(p, 16),
            Math.pow(p, 17),
            Math.pow(p, 18),
            Math.pow(p, 19),
            Math.pow(p, 20),
            Math.pow(p, 21),
            Math.pow(p, 22),
            Math.pow(p, 23),
            Math.pow(p, 24),
            Math.pow(p, 25),
            Math.pow(p, 26),
            Math.pow(p, 27),
            Math.pow(p, 28),
            Math.pow(p, 29),
            Math.pow(p, 30),
            Math.pow(p, 31),
    };

    public RDSLNode(T dataNode, int level) {
        this.dataNode = dataNode;
        this.references = new RDSLNode[level];
        this.distances = new int[level];
    }

    public int getHeadLevel() {
        // use to get current document level from RDSL head
        // is not equal to self level
//        int level = 1;
//        while(this.getRight(level) != null && level <= RDSLPath.MAX_LEVEL) {
//            level++;
//        }
//        return level - 1;
        return 0;
    }

    public int getLevel() {
        return this.references.length;
    }

    public String getContentString() {
        return "";
    }

    public RDSLNode getRight(int level) {
        return this.references[level - 1];
    }

    public void setRight(int level, RDSLNode<T> right) {
        this.references[level - 1] = right;
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

    protected int getRandomLevel() {
        double ran = Math.random();
        int level = 0;
        while(ran < RDSLNode.pArray[level]) {
            level++;
        }
        return level;
    }

    protected String getGapString(int gap) {
        return " ".repeat(gap);
    }

    protected String getNodeString(String c, int gap) {
        return String.format(
                "%" + gap +"s",
                c);
    }

    protected String getNodeString(int c, int gap) {
        return String.format(
                "%" + gap +"d",
                c);
    }
}
