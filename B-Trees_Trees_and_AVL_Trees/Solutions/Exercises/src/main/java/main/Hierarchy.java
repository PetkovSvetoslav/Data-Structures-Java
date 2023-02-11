package main;

import java.util.*;
import java.util.stream.Collectors;

public class Hierarchy<T> implements IHierarchy<T> {
    private static class Node<T> {
        Node<T> parent;
        T key;
        List<Node<T>> children;

        Node(T key) {
            this.key = key;
            this.children = new ArrayList<>();
        }

        void addChild(Node<T> node) {
            this.children.add(node);
            node.parent = this;
        }
    }

    private Node<T> root;
    private int count;
    private Map<T, Node<T>> nodeMap;

    public Hierarchy(T element) {
        this.root = new Node<>(element);
        this.count = 1;
        this.nodeMap = new LinkedHashMap<>();
        this.nodeMap.put(element, this.root);
    }

    @Override
    public int getCount() {
        return this.count;
    }

    @Override
    public void add(T element, T child) {
        Node<T> parent = this.find(element);

        if (parent == null || this.contains(child)) {
            throw new IllegalArgumentException();
        }

        Node<T> nodeToAdd = new Node<>(child);
        parent.addChild(nodeToAdd);

        this.nodeMap.put(child, nodeToAdd);

        this.count++;
    }

    @Override
    public void remove(T element) {
        Node<T> nodeToRemove = find(element);
        validateNode(nodeToRemove);

        if (nodeToRemove.parent == null) {
            throw new IllegalStateException();
        }

        Node<T> parent = nodeToRemove.parent;
        parent.children.remove(nodeToRemove);

        for (Node<T> child : nodeToRemove.children) {
            parent.addChild(child);
        }

        this.nodeMap.remove(element);

        this.count--;
    }

    @Override
    public Iterable<T> getChildren(T element) {
        Node<T> node = find(element);
        validateNode(node);

        return node.children
                .stream()
                .map(c -> c.key)
                .collect(Collectors.toList());
    }

    @Override
    public T getParent(T element) {
        Node<T> node = find(element);
        validateNode(node);

        return node.parent == null ? null : node.parent.key;
    }

    @Override
    public boolean contains(T element) {
        return this.nodeMap.containsKey(element);
    }

    @Override
    public Iterable<T> getCommonElements(IHierarchy<T> other) {
        List<T> commonElements = new ArrayList<>();

        for (T key : this) {
            if (other.contains(key)) {
                commonElements.add(key);
            }
        }

        return commonElements;
    }

    @Override
    public Iterator<T> iterator() {
        List<T> result = new ArrayList<>();

        Queue<Node<T>> queue = new ArrayDeque<>();
        queue.offer(this.root);

        while (!queue.isEmpty()) {
            Node<T> currentNode = queue.poll();

            result.add(currentNode.key);

            for (Node<T> child : currentNode.children) {
                queue.offer(child);
            }
        }

        return result.iterator();
    }

    private Node<T> find(T element) {
        return this.nodeMap.get(element);
    }

    private void validateNode(Node<T> node) {
        if (node == null) {
            throw new IllegalArgumentException();
        }
    }
}