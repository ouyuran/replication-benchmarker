package jbenchmarker.RDSL;

public class RDSLWalker<T> {
    private RDSLNode<T> currentNode;
    private int posLeft;
    private RDSLPath path;
    private int currentLevel;

    public RDSLWalker(RDSLNode start, int pos, int level) {
        this.currentNode = start;
        this.posLeft = pos;
        this.currentLevel = level;
        this.path = new RDSLPath();
        this.addCurrentFootPrint();
    }

    private void addCurrentFootPrint() {
        this.path.add(new RDSLFootPrint(this.currentNode, this.currentLevel));
    }

    public boolean shouldGoRight() {
        //todo
        return this.posLeft > this.currentNode.getRightDistance(this.currentLevel);
    }
    public void goRight() {
        this.posLeft -= this.currentNode.getRightDistance(this.currentLevel);
        this.currentNode = this.currentNode.getRight(this.currentLevel);
        this.addCurrentFootPrint();
    }

    public void goDown() {
        this.currentLevel --;
        this.addCurrentFootPrint();
    }

    public boolean canGo() {
        return this.currentLevel > 0 || this.currentNode.getRight(this.currentLevel) != null;
    }

    public boolean finish() {
        return this.currentLevel == 0 && (
                this.currentNode.getRight(this.currentLevel) == null ||
                        //todo
                        this.currentNode.getRightDistance(this.currentLevel) == 0
        );
    }

    public T getCurrentDataNode() {
        return this.currentNode.getDataNode();
    }

    public int getPosLeft() {
        return this.posLeft;
    }

}
