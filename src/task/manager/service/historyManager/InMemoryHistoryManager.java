package task.manager.service.historyManager;

import task.manager.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private ArrayList<Task> tasksHistoryList = new ArrayList<>();

    private HashMap<Integer, Node> tasksHistory = new HashMap<>();

    private Node head;
    private Node tail;
    private int size = 0;

    private void linkLast(Task task) {
        Node oldHead = head;
        Node newNode = new Node(task, oldHead, null);
        if (oldHead == null) {
            tail = newNode;
        } else {
            newNode = new Node(task, oldHead, null);
            oldHead.prev = newNode;
        }
        head = newNode;
        tasksHistory.put(task.getId(), newNode);
        size++;
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }

        Node next = node.next;
        Node prev = node.prev;
        if (next == null && prev == null) {
            tail = head = null;
        } else if (next == null) {
            prev.next = null;
            tail = prev;
        } else if (prev == null) {
            next.prev = null;
            head = next;
        } else {
            next.prev = prev;
            prev.next = next;
        }
        tasksHistory.remove(node.task.getId());
        size--;
    }

    public void remove(int id) {
        removeNode(tasksHistory.get(id));
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            System.out.println("Такой задачи нет");
            return;
        }
        if (tasksHistory.containsKey(task.getId())) {
            remove(task.getId());
        }
        linkLast(task);
    }

    private void getTasks() {
        tasksHistoryList = new ArrayList<>();
        Node node = tail;
        while (node != null) {
            tasksHistoryList.add(node.task);
            node = node.prev;
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        getTasks();
        return tasksHistoryList;
    }

    private static class Node {
        Task task;
        Node next;
        Node prev;

        public Node(Task task, Node next, Node prev) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }
    }
}



