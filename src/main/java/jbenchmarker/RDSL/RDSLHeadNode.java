package jbenchmarker.RDSL;

import java.util.ArrayList;

public class RDSLHeadNode<T extends RDSLWalkable> extends RDSLNode{
    protected int headLevel = 0;
    public RDSLHeadNode(T dataNode, int level) {
        super(dataNode, level);
    }

    public void setHeadLevel(int l) {
        this.headLevel = l;
    }

    public int getHeadLevel() {
        return this.headLevel;
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
            this.setHeadLevel(Math.max(level, this.getHeadLevel()));
        }
        //MyLogger.log(String.format("# %s, level %d", dataNode.getContentString(), level));
        for(int l = 1; l <= this.getHeadLevel(); l++) {
            RDSLNode left = this.getLastFootPrintOfLevel(path, l);
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
            RDSLNode left = this.getLastFootPrintOfLevel(path, l);
            RDSLNode right = left.getRight(l);
            if(right != null) {
                right.updateDistance(l, delta);
            }
        }
    }

    private RDSLNode getLastFootPrintOfLevel(RDSLPath<T> path, int level) {
        RDSLNode left = path.getLastRDSLNodeOfLevel(level);
        return left != null ? left : this;
    }
}
