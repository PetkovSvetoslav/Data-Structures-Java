package implementations;

import interfaces.AbstractTree;

import java.util.*;

public class Tree<E> implements AbstractTree<E> {
    private E key;
    private Tree<E> parent;
    private List<Tree<E>> children;

    @SafeVarargs
    public Tree(E key, Tree<E>... children) {
        this.key = key;
        this.children = new ArrayList<>();
        addChildren(children);

    }

    @Override
    public List<E> orderBfs() {
        List<E> nodes = new ArrayList<>();

        Tree<E> currentNode = this;

        Queue<Tree<E>> treeQueue = new ArrayDeque<>();
        treeQueue.offer(currentNode);

        while (!treeQueue.isEmpty()) {
            currentNode = treeQueue.poll();
            if (currentNode.getKey() != null) {
                nodes.add(currentNode.getKey());
            }

            for (Tree<E> child : currentNode.getChildren()) {
                treeQueue.offer(child);
            }
        }

        return nodes;
    }

    @Override
    public List<E> orderDfs() {
        List<E> nodes = new ArrayList<>();
        dfs(this, nodes);
        return nodes;
    }

    private void dfs(Tree<E> tree, List<E> nodes) {
        for (Tree<E> child : tree.getChildren()) {
            dfs(child, nodes);
        }
        nodes.add(tree.getKey());
    }

    @Override
    public void addChild(E parentKey, Tree<E> child) {
        Tree<E> parent = findNode(this, parentKey);
        if (parent != null) {
            parent.children.add(child);
            child.setParent(parent);
        }
    }

    private void addChild(Tree<E> child) {
        this.children.add(child);
    }

    @Override
    public void removeNode(E nodeKey) {
        Tree<E> nodeToRemove = findNode(this, nodeKey);

        if (nodeToRemove == null) {
            throw new IllegalArgumentException();
        }


        for (Tree<E> child : nodeToRemove.getChildren()) {
            child.setParent(null);
        }
        nodeToRemove.children.clear();

        Tree<E> parent = nodeToRemove.getParent();

        if (parent != null) {
            parent.getChildren().remove(nodeToRemove);
        }

        nodeToRemove.setValue(null);
    }

    @Override
    public void swap(E firstKey, E secondKey) {
        Tree<E> firstNode = findNode(this, firstKey);
        Tree<E> secondNode = findNode(this, secondKey);

        if (firstNode == null || secondNode == null) {
            throw new IllegalArgumentException();
        }

        Tree<E> firstParent = firstNode.getParent();
        Tree<E> secondParent = secondNode.getParent();

        firstNode.setParent(secondParent);
        secondNode.setParent(firstParent);

        int firstIndex = firstParent.getChildren().indexOf(firstNode);
        int secondIndex = secondParent.getChildren().indexOf(secondNode);

        firstParent.getChildren().set(firstIndex, secondNode);
        secondParent.getChildren().set(secondIndex, firstNode);
    }

    private Tree<E> copy(Tree<E> node) {
        if (node == null) {
            return null;
        }

        Tree<E> copiedNode = new Tree<>(node.key);
        for (Tree<E> child : node.getChildren()) {
            Tree<E> copiedChild = copy(child);
            copiedChild.setParent(copiedNode);
            copiedNode.addChild(copiedChild);
        }

        return copiedNode;
    }

    public E getKey() {
        return this.key;
    }

    private void setValue(E value) {
        this.key = value;
    }

    public Tree<E> getParent() {
        return this.parent;
    }

    public List<Tree<E>> getChildren() {
        return this.children;
    }

    private void setParent(Tree<E> parent) {
        this.parent = parent;
    }

    private void addChildren(Tree<E>[] children) {
        for (Tree<E> child : children) {
            this.children.add(child);
            child.setParent(this);
        }
    }

    private Tree<E> findNode(Tree<E> root, E nodeKey) {
        Tree<E> currentNode = root;

        Queue<Tree<E>> treeQueue = new ArrayDeque<>();
        treeQueue.offer(currentNode);

        while (!treeQueue.isEmpty()) {
            currentNode = treeQueue.poll();

            if (currentNode.getKey().equals(nodeKey)) {
                return currentNode;
            }

            for (Tree<E> child : currentNode.getChildren()) {
                treeQueue.offer(child);
            }
        }

        return null;
    }
}