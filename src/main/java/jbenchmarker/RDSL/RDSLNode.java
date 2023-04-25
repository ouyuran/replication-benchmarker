package jbenchmarker.RDSL;

import Tools.MyLogger;

import java.util.ArrayList;

public class RDSLNode<T extends RDSLWalkable> implements RDSLWalkable{
    private T dataNode;
    private RDSLNode[] references;
    private int[] distances;

    private static final double p = 1.0 / 4;

    private static final double[] pArray = {
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
        double ran = Math.random();
        int level = 0;
        while(ran < RDSLNode.pArray[level]) {
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
            System.out.println(outputs.get(i));
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
        //MyLogger.log(String.format("# %s, level %d", dataNode.getContentString(), level));
        for(int l = 1; l <= Math.max(level, this.getHeadLevel()); l++) {
            RDSLFootPrint leftFp = path.getLastFootPrintOfLevel(l);
            RDSLNode left = leftFp != null ? (RDSLNode) leftFp.getNode() : this;
            RDSLNode right = left.getRight(l);
            if(l <= level) {
                left.setRight(l, rdslNode);
                rdslNode.setRight(l, right);
                int selfDis = path.getDistance(l);
                rdslNode.updateDistance(l, selfDis);
                //MyLogger.log(String.format("Update self distance %s, level %d, delta %d", dataNode.getContentString(), l, selfDis));
                if(right != null) {
                    int rightDelta = dataNode.getDistance(0) - rdslNode.getDistance(l);
                    right.updateDistance(l, rightDelta);
                    //MyLogger.log(String.format("Update right distance %s, level %d, delta %d",
                    //        ((T) rdslNode.getRight(l).getDataNode()).getContentString(), l, rightDelta));
                }
            } else {
                if(right != null) {
                    right.updateDistance(l , dataNode.getDistance(0));
                    //MyLogger.log(String.format("Update right distance %s, level %d, delta %d",
                    //        ((T) right.getDataNode()).getContentString(), l, dataNode.getDistance(0)));
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
