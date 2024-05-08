package manager.history;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> nodes = new LinkedHashMap<>();
    private Node tail;

    @Override
    public void add(Task task) {
        if (nodes.containsKey(task.getId())) {
            remove(task.getId());
        }
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        nodes.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task task) {
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, task, null);
        if (oldTail == null) {
            tail = newNode;
        } else {
            oldTail.setPrev(newNode);
        }

        nodes.put(task.getId(), newNode);
    }

    private List<Task> getTasks() {
        List<Task> history = new ArrayList<>();
        for (Node node : nodes.values()) {
            history.add(node.getData());
        }
        return history;
    }
}

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