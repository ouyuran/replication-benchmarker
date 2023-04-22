package jbenchmarker.RDSL;

import Tools.MyLogger;

import java.util.ArrayList;

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

    public int getHeadLevel() {
        // use to get current document level from RDSL head
        // is not equal to self level
        int level = 1;
        while(this.getRight(level) != null && level <= RDSLPath.MAX_LEVEL) {
            level++;
        }
        return level - 1;
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

    private int getRandomLevel() {
        int level = 0;
        while(Math.random() < p && level <= RDSLPath.MAX_LEVEL) {
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
        ArrayList<String> outputs = new ArrayList<>();
        String s = getNodeString("*", gap);
        T current = (T) this.dataNode.getRight(0);
        while(current != null) {
            s += getNodeString((String) current.getContentString(), gap);
            current = (T) current.getRight(0);
        }
        outputs.add(s);
        for(int i = 1; i <= RDSLPath.MAX_LEVEL; i++){
            if(this.getRight(i) == null) continue;
            s = getNodeString("0", gap);
            current = (T) this.dataNode.getRight(0);
            while (current != null) {
                RDSLNode currentRDSLNode = this.findRDSLNode(current);
                if(currentRDSLNode != null && currentRDSLNode.getLevel() >= i) {
                    s += getNodeString(currentRDSLNode.getDistance(i), gap);
                } else {
                    s += getGapString(gap);
                }
                current = (T) current.getRight(0);
            }
            outputs.add(s);
        }
        for(int i = outputs.size() - 1; i >=0; i--) {
            MyLogger.log(outputs.get(i));
        }
    }

    private RDSLNode findRDSLNode(T dataNode) {
        RDSLNode currentRDSLNode = this.references[0];
        while(currentRDSLNode != null) {
            if (currentRDSLNode.getDataNode() == dataNode) {
                return currentRDSLNode;
            }
            currentRDSLNode = currentRDSLNode.getRight(1);
        }
        return null;
    }

    public void handleInsert(T dataNode, RDSLPath<T> path) {
        // always call this from rdslHead
        int level = this.getRandomLevel();
        RDSLNode<T> rdslNode = null;
        if(level > 0) {
            rdslNode = new RDSLNode<T>(dataNode, level);
        }
        MyLogger.log(String.format("# %s, level %d", dataNode.getContentString(), level));
        for(int l = 1; l <= Math.max(level, this.getHeadLevel()); l++) {
            RDSLFootPrint leftFp = path.getLastFootPrintOfLevel(l);
            RDSLNode left = leftFp != null ? (RDSLNode) leftFp.getNode() : this;
            RDSLNode right = left.getRight(l);
            if(l <= level) {
                left.setRight(l, rdslNode);
                rdslNode.setRight(l, right);
                int selfDis = path.getDistance(l);
                rdslNode.updateDistance(l, selfDis);
                MyLogger.log(String.format("Update self distance %s, level %d, delta %d", dataNode.getContentString(), l, selfDis));
                if(right != null) {
                    int rightDelta = dataNode.getDistance(0) - rdslNode.getDistance(l);
                    right.updateDistance(l, rightDelta);
                    MyLogger.log(String.format("Update right distance %s, level %d, delta %d",
                            ((T) rdslNode.getRight(l).getDataNode()).getContentString(), l, rightDelta));
                }
            } else {
                if(right != null) {
                    right.updateDistance(l , dataNode.getDistance(0));
                    MyLogger.log(String.format("Update right distance %s, level %d, delta %d",
                            ((T) right.getDataNode()).getContentString(), l, dataNode.getDistance(0)));
                }
            }
        }
    }

    public void handleUpdate(T dataNode, RDSLPath<T> path, int delta) {
        for(int l = 1; l <= this.getHeadLevel(); l++) {
            RDSLFootPrint leftFp = path.getLastFootPrintOfLevel(l);
            RDSLNode left = leftFp != null ? (RDSLNode) leftFp.getNode() : this;
            RDSLNode right = left.getRight(l);
            if(right != null) {
                right.updateDistance(l, delta);
            }
        }
    }
}
