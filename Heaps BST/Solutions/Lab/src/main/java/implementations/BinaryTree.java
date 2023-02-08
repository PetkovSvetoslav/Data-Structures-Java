package implementations;

import interfaces.AbstractBinaryTree;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BinaryTree<E> implements AbstractBinaryTree<E> {
    private E key;
    private final AbstractBinaryTree<E> left;
    private final AbstractBinaryTree<E> right;

    public BinaryTree(E key, AbstractBinaryTree<E> left, AbstractBinaryTree<E> right) {
        this.key = key;
        this.left = left;
        this.right = right;
    }

    @Override
    public E getKey() {
        return this.key;
    }

    @Override
    public AbstractBinaryTree<E> getLeft() {
        return this.left;
    }

    @Override
    public AbstractBinaryTree<E> getRight() {
        return this.right;
    }

    @Override
    public void setKey(E key) {
        this.key = key;
    }

    @Override
    public String asIndentedPreOrder(int indent) {
        StringBuilder buffer = new StringBuilder();

        this.getTree(buffer, this, indent);

        return buffer.toString().trim();
    }

    private void getTree(StringBuilder buffer, AbstractBinaryTree<E> node, int indent) {
        if (node == null) {
            return;
        }

        buffer.append("  ".repeat(Math.max(0, indent)));

        indent++;

        buffer.append(node.getKey())
                .append(System.lineSeparator());
        this.getTree(buffer, node.getLeft(), indent);
        this.getTree(buffer, node.getRight(), indent);
    }

    @Override
    public List<AbstractBinaryTree<E>> preOrder() {
        List<AbstractBinaryTree<E>> tree = new ArrayList<>();

        this.preOrder(this, tree);

        return tree;
    }

    private void preOrder(AbstractBinaryTree<E> node, List<AbstractBinaryTree<E>> tree) {
        if (node == null) {
            return;
        }
        tree.add(node);
        this.preOrder(node.getLeft(), tree);
        this.preOrder(node.getRight(), tree);
    }

    @Override
    public List<AbstractBinaryTree<E>> inOrder() {
        List<AbstractBinaryTree<E>> tree = new ArrayList<>();

        this.inOrder(this, tree);

        return tree;
    }

    private void inOrder(AbstractBinaryTree<E> node, List<AbstractBinaryTree<E>> tree) {
        if (node == null) {
            return;
        }

        this.inOrder(node.getLeft(), tree);
        tree.add(node);
        this.inOrder(node.getRight(), tree);
    }

    @Override
    public List<AbstractBinaryTree<E>> postOrder() {
        List<AbstractBinaryTree<E>> tree = new ArrayList<>();

        postOrder(this, tree);

        return tree;
    }

    private void postOrder(AbstractBinaryTree<E> node, List<AbstractBinaryTree<E>> tree) {
        if (node == null) {
            return;
        }

        this.postOrder(node.getLeft(), tree);
        this.postOrder(node.getRight(), tree);
        tree.add(node);
    }

    @Override
    public void forEachInOrder(Consumer<E> consumer) {
        if (this.getLeft() != null) {
            this.getLeft().forEachInOrder(consumer);
        }

        consumer.accept(this.key);

        if (this.getRight() != null) {
            this.getRight().forEachInOrder(consumer);
        }
    }
}