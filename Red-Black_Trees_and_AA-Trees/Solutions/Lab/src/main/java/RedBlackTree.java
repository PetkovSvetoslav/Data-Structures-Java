import java.util.function.Consumer;

public class RedBlackTree<T extends Comparable<T>> {
    private static final boolean RED = true;
    private static final boolean BLACK = false;

    public static class Node<T extends Comparable<T>> {
        private T value;
        private Node<T> left;
        private Node<T> right;
        private boolean color;
        private int count;

        public Node() {
        }

        private Node(T value, boolean color) {
            this.value = value;
            this.color = color;
        }

        @Override
        public String toString() {
            return (this.color ? "R" : "B") + "(" + value + ")";
        }
    }

    private Node<T> root;

    public RedBlackTree() {
    }

    private RedBlackTree(Node<T> node) {
        this.preOrderCopy(node);
    }

    private boolean isRed(Node<T> node) {
        return node != null && node.color != BLACK;
    }

    private Node<T> leftRotate(Node<T> node) {
        Node<T> right = node.right;
        node.right = right.left;
        right.left = node;
        right.color = node.color;
        node.color = RED;
        node.count = 1 + count(node.left) + count(node.right);

        return right;
    }

    private Node<T> rightRotate(Node<T> node) {
        Node<T> left = node.left;
        node.left = left.right;
        left.right = node;
        left.color = node.color;
        node.color = RED;
        node.count = 1 + count(node.left) + count(node.right);

        return left;
    }

    private void flipColors(Node<T> node) {
        if (node == null) {
            return;
        }

        node.color = RED;

        if (node.left != null) {
            node.left.color = BLACK;
        }

        if (node.right != null) {
            node.right.color = BLACK;
        }
    }

    private int count(Node<T> node) {
        if (node == null) {
            return 0;
        }

        return node.count;
    }

    private void preOrderCopy(Node<T> node) {
        if (node == null) {
            return;
        }

        this.insert(node.value);
        this.preOrderCopy(node.left);
        this.preOrderCopy(node.right);
    }

    public int getNodesCount() {
        return this.getNodesCount(this.root);
    }

    private int getNodesCount(Node<T> node) {
        if (node == null) {
            return 0;
        }
        return node.count;
    }

    // TODO:
    //  Insert using iteration over the nodes
    //  You can make a recursive one it is up to you
    //  The difference is that the recursive call should
    //  return Node
    public void insert(T value) {
        this.root = this.insert(value, this.root);
        this.root.color = BLACK;
    }

    private Node<T> insert(T value, Node<T> node) {
        if (node == null) {
            node = new Node<>(value, RED);
        } else if (this.isLessThan(value, node.value)) {
            node.left = this.insert(value, node.left);
        } else if (this.isGreaterThan(value, node.value)) {
            node.right = this.insert(value, node.right);
        }

        if (this.isRed(node.right) && !this.isRed(node.left)) {
            if (this.isRed(node.right.left)) {
                node.right = rightRotate(node.right);
            }

            if (this.isRed(node.right.right)) {
                node = this.leftRotate(node);
            }
        }

        if (this.isRed(node.left) && !this.isRed(node.right)) {
            if (this.isRed(node.left.right)) {
                node.left = leftRotate(node.left);
            }

            if (isRed(node.left.left)) {
                node = this.rightRotate(node);
            }
        }

        if (this.isRed(node.left) && this.isRed(node.right)) {
            this.flipColors(node);
        }

        node.count = 1 + this.count(node.left) + this.count(node.right);
        return node;
    }

    private boolean isLessThan(T a, T b) {
        return a.compareTo(b) < 0;
    }

    private boolean isGreaterThan(T a, T b) {
        return a.compareTo(b) > 0;
    }

    public boolean contains(T value) {
        return this.findElement(value) != null;
    }

    public RedBlackTree<T> search(T item) {
        return new RedBlackTree<>(this.findElement(item));
    }

    private Node<T> findElement(T item) {
        Node<T> current = this.root;
        while (current != null) {
            if (item.compareTo(current.value) < 0) {
                current = current.left;
            } else if (item.compareTo(current.value) > 0) {
                current = current.right;
            } else {
                break;
            }
        }
        return current;
    }

    public void eachInOrder(Consumer<T> consumer) {
        this.eachInOrder(this.root, consumer);
    }

    private void eachInOrder(Node<T> node, Consumer<T> consumer) {
        if (node == null) {
            return;
        }

        this.eachInOrder(node.left, consumer);
        consumer.accept(node.value);
        this.eachInOrder(node.right, consumer);
    }

    public void printPreOrder() {
        StringBuilder buffer = new StringBuilder();
        getInPreOrder(this.root, 0, buffer);
        System.out.println(buffer);
    }

    private void getInPreOrder(Node<T> node, int level, StringBuilder buffer) {
        if (node == null) {
            return;
        }

        buffer.append("    ".repeat(Math.max(0, level)));

        buffer.append(node).append(System.lineSeparator());
        getInPreOrder(node.left, level + 1, buffer);
        getInPreOrder(node.right, level + 1, buffer);
    }
}