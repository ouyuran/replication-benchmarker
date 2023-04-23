package jbenchmarker.logootSplitRDSL;

import jbenchmarker.RDSL.RDSLWalkable;
import jbenchmarker.logoot.ListIdentifier;

import java.util.List;

public class LogootSplitRDSLNode<T> implements RDSLWalkable {
    private List<T> content;
    private LogootSplitRDSLNode<T> next;
    private ListIdentifier id;
    public LogootSplitRDSLNode(List<T> content, ListIdentifier id) {
        this.content = content;
        this.id = id;
    }

    private void setNext(LogootSplitRDSLNode next) {
        this.next = next;
    }

    public void addAfter(LogootSplitRDSLNode<T> node) {
        node.setNext(this.next);
        this.next = node;
    }

    public void removeAfter() {
        this.next = this.next.next;
    }

    @Override
    public int getDistance(int level) {
        return this.getContentString().length();
    }

    @Override
    public RDSLWalkable getRight(int level) {
        return this.next;
    }

    public String getContentString() {
        if(content == null) return "";
        return String.valueOf(content);
    }

    public ListIdentifier getId() {
        return this.id;
    }
}

