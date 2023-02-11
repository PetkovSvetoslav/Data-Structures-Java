import java.util.function.Consumer;

public class AVL<T extends Comparable<T>> {

    private Node<T> root;

    public Node<T> getRoot() {
        return this.root;
    }

    public int height() {
        return height(this.root);
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

    public void delete(T item) {
        this.root = this.delete(item, this.root);
    }

    private Node<T> delete(T item, Node<T> node) {
        if (node == null) {
            return null;
        }

        int comp = item.compareTo(node.value);

        if (comp < 0) {
            this.delete(item, node.left);
        } else if (comp > 0) {
            this.delete(item, node.right);
        } else {
            if (node.left == null) {
                return node.right;
            } else if (node.right == null) {
                return node.left;
            } else {
                Node<T> min = getMin(node.right);
                min.right = deleteMin(node.right);
                min.left = node.left;
                node = min;
            }
        }

        node = balance(node);
        return node;
    }

    public void deleteMin() {
        this.root = this.deleteMin(this.root);
    }

    private Node<T> deleteMin(Node<T> node) {
        if (node == null) {
            return null;
        }

        if (node.left == null) {
            return node.right;
        }

        node.left = this.deleteMin(node.left);
        node = this.balance(node);

        return node;
    }

    public void deleteMax() {
        this.root = this.deleteMax(this.root);
    }

    private Node<T> deleteMax(Node<T> node) {
        if (node == null) {
            return null;
        }

        if (node.right == null) {
            return node.left;
        }

        node.left = deleteMax(node.right);
        node = balance(node);

        return node;
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

        this.updateHeight(node);

        return balance(node);
    }

    private Node<T> getMin(Node<T> node) {
        if (node == null) {
            return null;
        }

        if (node.left == null) {
            return node;
        }

        return getMin(node.left);
    }

    private Node<T> rotateLeft(Node<T> node) {
        Node<T> right = node.right;
        node.right = right.left;
        right.left = node;

        this.updateHeight(node);
        this.updateHeight(right);

        return right;
    }

    private Node<T> rotateRight(Node<T> node) {
        Node<T> left = node.left;
        node.left = node.left.right;
        left.right = node;

        this.updateHeight(node);
        this.updateHeight(left);

        return left;
    }

    private Node<T> balance(Node<T> node) {
        int balance = this.balanceFactor(node);

        if (balance < -1) {
            int childBalance = this.balanceFactor(node.right);
            if (childBalance > 0) {
                node.right = rotateRight(node.right);
            }
            return rotateLeft(node);

        } else if (balance > 1) {
            int childBalance = this.balanceFactor(node.left);
            if (childBalance < 0) {
                node.left = this.rotateRight(node.left);
            }
            return this.rotateRight(node);
        }

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

    private int balanceFactor(Node<T> node) {
        return height(node.left) - height(node.right);
    }

    private int height(Node<T> node) {
        if (node == null) {
            return 0;
        }
        return node.height;
    }

    private void updateHeight(Node<T> node) {
        node.height = Math.max(this.height(node.left), this.height(node.right)) + 1;
    }
}
