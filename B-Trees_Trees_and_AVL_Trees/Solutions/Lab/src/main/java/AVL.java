import java.util.function.Consumer;

public class AVL<T extends Comparable<T>> {
    public static class Node<T extends Comparable<T>> {
        public T value;
        public Node<T> left;
        public Node<T> right;

        public int height;

        public Node(T value) {
            this.value = value;
            this.height = 1;
        }
    }

    private Node<T> root;

    public Node<T> getRoot() {
        return this.root;
    }

    public boolean contains(T item) {
        Node<T> node = this.search(this.root, item);
        return node != null;
    }

    public void insert(T item) {
        this.root = this.insert(this.root, item);
    }

    public void eachInOrder(Consumer<T> consumer) {
        this.eachInOrder(this.root, consumer);
    }

    private void eachInOrder(Node<T> node, Consumer<T> action) {
        if (node == null) {
            return;
        }

        this.eachInOrder(node.left, action);
        action.accept(node.value);
        this.eachInOrder(node.right, action);
    }

    private Node<T> insert(Node<T> node, T item) {
        if (node == null) {
            return new Node<>(item);
        }

        int cmp = item.compareTo(node.value);
        if (cmp < 0) {
            node.left = this.insert(node.left, item);
        } else if (cmp > 0) {
            node.right = this.insert(node.right, item);
        }

        node = balance(node);
        updateHeight(node);
        return node;
    }

    private Node<T> search(Node<T> node, T item) {
        if (node == null) {
            return null;
        }

        int cmp = item.compareTo(node.value);
        if (cmp < 0) {
            return search(node.left, item);
        } else if (cmp > 0) {
            return search(node.right, item);
        }

        return node;
    }

    private Node<T> leftRotate(Node<T> node) {
        Node<T> right = node.right;
        node.right = node.right.left;
        right.left = node;

        this.updateHeight(node);

        return right;
    }

    private Node<T> rightRotate(Node<T> node) {
        Node<T> left = node.left;
        node.left = node.left.right;
        left.right = node;

        this.updateHeight(node);

        return left;
    }

    private int getBalance(Node<T> node) {
        return node == null ? 0 : this.height(node.left) - this.height(node.right);
    }

    private Node<T> balance(Node<T> node) {
        int balance = this.getBalance(node);

        if (balance < -1) {
            int childBalance = this.getBalance(node.right);
            if (childBalance > 0) {
                node.right = this.rightRotate(node.right);
            }
            return this.leftRotate(node);

        } else if (balance > 1) {
            int childBalance = this.getBalance(node.left);
            if (childBalance < 0) {
                node.left = this.leftRotate(node.left);
            }
            return this.rightRotate(node);
        }

        return node;
    }

    private int height(Node<T> node) {
        if (node == null) {
            return 0;
        }

        return node.height;
    }

    private void updateHeight(Node<T> node) {
        node.height = Math.max(height(node.left), height(node.right)) + 1;
    }
}