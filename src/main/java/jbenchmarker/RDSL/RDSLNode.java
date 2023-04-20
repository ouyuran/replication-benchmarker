package jbenchmarker.RDSL;

import jbenchmarker.rga.RGANode;

public class RDSLNode<T extends RDSLWalkable> implements RDSLWalkable{
    private T dataNode;
    private RDSLNode[] references;
    private int[] distances;

    private static final double p = 1.0 / 2;

    public RDSLNode(T dataNode, int level) {
        this.dataNode = dataNode;
        this.references = new RDSLNode[level];
        this.distances = new int[level];
    }

    public int getLevel() {
        // use to get current document level from RDSL head
        // is not equal to self level
        int level = 1;
        while(this.getRight(level) != null && level <= RDSLPath.MAX_LEVEL) {
            level++;
        }
        return level;
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

    public static int getRandomLevel() {
        int level = 0;
        while(Math.random() < p) {
            level++;
        }
        return level;
    }

    private String getGapString(int gap) {
        return " ".repeat(gap);
    }

    private String getNodeString(String c, int gap) {
        return String.format(
                "%" + gap +"s",
                c);
    }

    private String getNodeString(int c, int gap) {
        return String.format(
                "%" + gap +"d",
                c);
    }
    public void print(int gap) {
        for(int i = RDSLPath.MAX_LEVEL; i > 0; i--) {
            if(this.getRight(i) == null) continue;
            String s = "";
            RDSLNode current = this;
            int index = 0;
            while(current != null) {
                int distance = current.getDistance(i);
                if (distance == 0) {
                    s += getNodeString(distance, gap);
                } else {
                    s = s + getGapString(gap).repeat(index < 2 ? distance : distance - 1) + getNodeString(distance, gap);
                }
                current = current.getRight(i);
                index++;
            }
            System.out.println(s);
        }
        String s = getNodeString("*", gap);
        T current = (T) this.dataNode.getRight(0);
        while(current != null) {
            s += getNodeString((String) current.getContentString(), gap);
            current = (T) current.getRight(0);
        }
        System.out.println(s);
    }
}
