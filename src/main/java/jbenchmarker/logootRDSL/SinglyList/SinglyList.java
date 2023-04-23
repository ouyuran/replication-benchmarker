package jbenchmarker.logootRDSL.SinglyList;

public class SinglyList<T> {
    private Node<T> head;
    public SinglyList() {
        this.head = new Node<>(null);
    }

    public Node getHead() {
        return head;
    }
}
