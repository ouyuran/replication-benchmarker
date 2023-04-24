package jbenchmarker.RDSL;

import Tools.MyLogger;

public class RDSLWalker<T extends RDSLWalkable> {
    protected RDSLNode<T> currentNode;
    protected T dataNode;
    private int posLeft;
    private RDSLPath path;
    protected int currentLevel;

    public RDSLWalker(RDSLNode start, int pos, int level) {
        this.currentNode = start;
        this.posLeft = pos;
        this.currentLevel = level;
        this.path = new RDSLPath(level);
        this.addCurrentFootPrint();
        this.dataNode = null;
    }

    public RDSLWalker(RDSLNode start, int pos, int level, RDSLPath<T> path) {
        this.currentNode = start;
        this.posLeft = pos;
        this.currentLevel = level;
        this.path = path;
        this.addCurrentFootPrint();
        this.dataNode = null;
    }

    private void addCurrentFootPrint() {
        if(this.currentLevel > 0) {
            this.path.add(new RDSLFootPrint(this.currentNode, this.currentLevel));
        } else {
            this.path.addLevel0(new RDSLFootPrint(this.dataNode, 0));
        }
    }

    public boolean shouldGoRight() {
        if(this.currentLevel > 0) {
            return this.posLeft > this.currentNode.getRightDistance(this.currentLevel);
        } else {
            return this.posLeft > 0;
        }
    }
    public void goRight() {
        //MyLogger.log("goRight");
        if(this.currentLevel > 0) {
            RDSLNode right = this.currentNode.getRight(this.currentLevel);
            this.posLeft -= right.getDistance(this.currentLevel);
            this.currentNode = right;
        } else {
            T right = (T) this.dataNode.getRight(0);
            this.posLeft -= right.getDistance(0);
            this.dataNode = right;
        }
        this.addCurrentFootPrint();
    }

    public void goDown() {
        //MyLogger.log(String.format("goDown currentLevel %d, posLeft %d", this.currentLevel, this.posLeft));
        this.currentLevel --;
        if(this.currentLevel == 0) {
            this.dataNode = this.currentNode.getDataNode();
            this.posLeft -= this.dataNode.getDistance(0);
        }
        this.addCurrentFootPrint();
    }

    public boolean finish() {
        if(this.currentLevel < 0) {
            System.out.println("@@@@@@@@@ Should not goes here @@@@@@@@@@");
            return true;
        }
        return this.posLeft <= 0 && this.currentLevel == 0;
    }

    public T getCurrentDataNode() {
        return this.currentNode.getDataNode();
    }

    public int getPosLeft() {
        return this.posLeft;
    }

    public RDSLPath getPath() {
        return this.path;
    }

}
