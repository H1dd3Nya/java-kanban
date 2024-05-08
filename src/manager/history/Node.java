package manager.history;

import model.Task;

class Node {
    private Node next;
    private final Task data;
    private Node prev;

    public Node(Node prev, Task data, Node next) {
        this.next = next;
        this.data = data;
        this.prev = prev;
    }

    protected void setPrev(Node prev) {
        this.prev = prev;
    }

    protected Task getData() {
        return data;
    }
}
