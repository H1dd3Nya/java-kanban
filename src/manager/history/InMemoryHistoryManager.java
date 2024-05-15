package manager.history;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> history = new HashMap<>();
    private Node head = new Node(null, null, null);
    private Node tail;

    @Override
    public void add(Task task) {
        if (history.containsKey(task.getId())) {
            Node node = history.get(task.getId());
            removeNode(node);
        }
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        if (history.containsKey(id)) {
            Node node = history.get(id);
            removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task task) {
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.setNext(newNode);
        }
        history.put(task.getId(), newNode);
    }

    private void unlink(Node node) {
        Node next = node.getNext();
        Node prev = node.getPrev();

        if (prev == null) {
            head = next;
        } else {
            prev.setNext(next);
        }

        if (next == null) {
            tail = prev;
        } else {
            next.setPrev(prev);
        }
    }

    private void removeNode(Node node) {
        unlink(node);
        history.remove(node.getData().getId());
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node current = head;
        while (current != null) {
            tasks.add(current.getData());
            current = current.getNext();
        }
        return tasks;
    }
}

