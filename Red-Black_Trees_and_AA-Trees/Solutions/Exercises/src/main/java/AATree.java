import java.util.function.Consumer;

class AATree<E extends Comparable<E>> {
    public static class Node<E extends Comparable<E>> {
        private E element;
        private Node<E> left;
        private Node<E> right;
        private int level;
        private int count;

        private Node(E element, Node<E> left, Node<E> right) {
            this.element = element;
            this.left = left;
            this.right = right;
            this.level = 1;
            this.count = 1 + count(this.left) + this.count(this.right);
        }

        private Node(E element) {
            this(element, null, null);
        }

        private int count(Node<E> node) {
            return node == null ? 0 : node.count;
        }
    }

    private Node<E> root;

    public AATree() {
    }

    public boolean isEmpty() {
        return root == null;
    }

    public void clear() {
        this.root = null;
    }

    public void insert(E element) {
        this.root = this.insert(this.root, element);
    }

    private Node<E> insert(Node<E> node, E element) {
        if (node == null) {
            return new Node<>(element);
        } else {
            int comp = element.compareTo(node.element);

            if (comp < 0) {
                node.left = this.insert(node.left, element);
            } else if (comp > 0) {
                node.right = this.insert(node.right, element);
            } else {
                return node;
            }
        }

        node = this.skew(node);
        node = this.split(node);

        this.resize(node);

        return node;
    }

    private void resize(Node<E> node) {
        node.count = 1 + this.count(node.left) + this.count(node.right);
    }

    private Node<E> skew(Node<E> node) {
        if (node == null) {
            return null;
        } else if (node.left == null) {
            return node;
        } else if (node.left.level == node.level) {
            Node<E> left = node.left;
            node.left = left.right;
            left.right = node;

            this.resize(node);
            left.count++;

            return left;
        }

        return node;
    }

    private Node<E> split(Node<E> node) {
        if (node == null) {
            return null;
        } else if (node.right == null || node.right.right == null) {
            return node;
        } else if (node.level == node.right.right.level) {
            Node<E> right = node.right;
            node.right = right.left;
            right.left = node;

            this.resize(node);
            right.count++;

            right.level = right.level + 1;
            return right;
        }

        return node;
    }

    private int count(Node<E> node) {
        return node == null ? 0 : node.count;
    }

    public int countNodes() {
        return this.root == null ? 0 : this.root.count;
    }

    public boolean search(E element) {
        Node<E> currentNode = this.root;

        while (currentNode != null) {
            int comp = element.compareTo(currentNode.element);

            if (comp < 0) {
                currentNode = currentNode.left;
            } else if (comp > 0) {
                currentNode = currentNode.right;
            } else {
                return true;
            }
        }

        return false;
    }

    public void inOrder(Consumer<E> consumer) {
        this.inOrder(this.root, consumer);
    }

    private void inOrder(Node<E> node, Consumer<E> consumer) {
        if (node == null) {
            return;
        }

        this.inOrder(node.left, consumer);

        consumer.accept(node.element);

        this.inOrder(node.right, consumer);
    }

    public void preOrder(Consumer<E> consumer) {
        this.preOrder(this.root, consumer);
    }

    private void preOrder(Node<E> node, Consumer<E> consumer) {
        if (node == null) {
            return;
        }

        consumer.accept(node.element);

        this.preOrder(node.left, consumer);
        this.preOrder(node.right, consumer);

    }

    public void postOrder(Consumer<E> consumer) {
        this.postOrder(this.root, consumer);
    }

    private void postOrder(Node<E> node, Consumer<E> consumer) {
        if (node == null) {
            return;
        }
        
        this.postOrder(node.left, consumer);
        this.postOrder(node.right, consumer);

        consumer.accept(node.element);
    }
}