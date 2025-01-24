package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InMemoryHistoryManager implements HistoryManager {

    private static class Node {
        private Task data;
        private Node prev;
        private Node next;

        public Node(Task data, Node prev, Node next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }
    }

    private Node first;
    private Node last;
    private final Map<Integer, Node> listOfNodesInHistory = new HashMap<>();

    @Override
    public List<Task> getHistory() {
        List<Task> historyList = new ArrayList<>();
        if (first == null) return null;
        Node node = first;
        historyList.add(node.data);
        while (node.next != null) {
            node = node.next;
            historyList.add(node.data);
        }
        return historyList;
    }

    @Override
    public void addToHistory(Task task) {
        Integer id = task.getId();

        if (listOfNodesInHistory.containsKey(id)) {
            remove(id);
        }
        if (first == null) {
            first = new Node(task, null, null);
            listOfNodesInHistory.put(id, first);
        } else if (last == null) {
            last = new Node(task, first, null);
            first.next = last;
            listOfNodesInHistory.put(id, last);
        } else {
            Node oldLast = last;
            last = new Node(task, oldLast, null);
            oldLast.next = last;
            listOfNodesInHistory.put(id, last);
        }
    }

    private void removeNode(Node node) {
        if (node == first) {
            node.next.prev = null;
            first = node.next;
        } else if (node == last) {
            node.prev.next = null;
            last = node.prev;
        } else {
            Node prevNode = node.prev;
            Node nextNode = node.next;
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        }
    }

    @Override
    public void remove(int id) {
        if (listOfNodesInHistory.containsKey(id)) {
            Node nodeToRemove = listOfNodesInHistory.get(id);
            removeNode(nodeToRemove);
            listOfNodesInHistory.remove(id);
        }
    }
}
