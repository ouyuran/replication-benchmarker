package jbenchmarker.logootRDSL.SinglyList;

import jbenchmarker.RDSL.RDSLWalkable;

public class Node<T> implements RDSLWalkable {
    private T item;
    private Node<T> next;

    public Node(T item) {
        this.item = item;
    }

    private void setNext(Node next) {
        this.next = next;
    }

    public void addAfter(Node<T> node) {
        node.setNext(this.next);
        this.next = node;
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
        return String.valueOf(item);
    }
}
