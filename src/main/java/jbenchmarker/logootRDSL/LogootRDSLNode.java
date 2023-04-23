package jbenchmarker.logootRDSL;

import jbenchmarker.RDSL.RDSLWalkable;
import jbenchmarker.logoot.ListIdentifier;

import java.io.Serializable;

public class LogootRDSLNode<T> implements RDSLWalkable {
    private T content;
    private LogootRDSLNode<T> next;
    private ListIdentifier id;
    public LogootRDSLNode(T content, ListIdentifier id) {
        this.content = content;
        this.id = id;
    }

    private void setNext(LogootRDSLNode next) {
        this.next = next;
    }

    public void addAfter(LogootRDSLNode<T> node) {
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

