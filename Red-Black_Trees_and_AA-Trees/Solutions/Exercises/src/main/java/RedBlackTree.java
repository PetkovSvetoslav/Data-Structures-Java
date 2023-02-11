import java.util.*;

public class RedBlackTree<Key extends Comparable<Key>, Value> {

    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private Node root;     // root of the BST

    // BST helper node data type
    private class Node {
        private Key key;           // key
        private Value val;         // associated data
        private Node left, right;  // links to left and right subtrees
        private boolean color;     // color of parent link
        private int size;          // subtree count

        public Node(Key key, Value val, boolean color, int size) {
            this.key = key;
            this.val = val;
            this.color = color;
            this.size = size;
        }

        @Override
        public String toString() {
            return (this.color ? "R" : "B") + "(" + this.key + ")";
        }
    }

    public RedBlackTree() {
    }

    // is node node red; false if node is null ?
    private boolean isRed(Node node) {
        if (node == null) {
            return BLACK;
        }

        return node.color;
    }

    /**
     * Returns the number of key-value pairs in this symbol table.
     *
     * @return the number of key-value pairs in this symbol table
     */
    public int size() {
        return this.size(this.root);
    }

    // number of node in subtree rooted at x; 0 if x is null
    private int size(Node node) {
        if (node == null) {
            return 0;
        }

        return node.size;
    }

    /**
     * Is this symbol table empty?
     *
     * @return {@code true} if this symbol table is empty and {@code false} otherwise
     */
    public boolean isEmpty() {
        return this.root == null;
    }

    public Value get(Key key) {
        Node currentNode = this.root;

        while (currentNode != null) {
            if (isLessThan(key, currentNode.key)) {
                currentNode = currentNode.left;
            } else if (isGreaterThan(key, currentNode.key)) {
                currentNode = currentNode.right;
            } else {
                return currentNode.val;
            }
        }

        return null;
    }

    public boolean contains(Key key) {
        Node node = this.root;

        while (node != null) {
            if (isLessThan(key, node.key)) {
                node = node.left;
            } else if (isGreaterThan(key, node.key)) {
                node = node.right;
            } else {
                return true;
            }
        }

        return false;
    }

    // A variable that takes care of whether to set a value or rebalance the tree.
    private boolean setElement = false;

    public void put(Key key, Value val) {
        this.setElement = false;
        this.root = put(this.root, key, val);
        this.root.color = BLACK;
    }

    // insert the key-value pair in the subtree rooted at node
    private Node put(Node node, Key key, Value val) {

        if (node == null) {
            node = new Node(key, val, RED, 1);
        } else if (this.isLessThan(key, node.key)) {
            node.left = put(node.left, key, val);
        } else if (this.isGreaterThan(key, node.key)) {
            node.right = put(node.right, key, val);
        } else {
            this.setElement = true;
            node.val = val;
        }

        if (!this.setElement) {
            node = this.balance(node);
        }

        return node;
    }

    private void swapColors(Node a, Node b) {
        boolean swap = a.color;
        a.color = b.color;
        b.color = swap;
    }

    public void deleteMin() {
        ensureNonEmpty();
        this.root = deleteMin(this.root);
    }

    private void ensureNonEmpty() {
        if (this.isEmpty()) {
            throw new IllegalStateException("You cannot apply this method to an empty collection");
        }
    }

    // delete the key-value pair with the minimum key rooted at node
    private Node deleteMin(Node node) {
        if (node.left == null) {
            return node.right;
        }

        node.left = this.deleteMin(node.left);
        this.resize(node);

        return node;
    }

    public void deleteMax() {
        ensureNonEmpty();
        this.root = this.deleteMax(this.root);
    }

    // delete the key-value pair with the maximum key rooted at node
    private Node deleteMax(Node node) {
        if (node.right == null) {
            return node.left;
        }

        node.right = this.deleteMax(node.right);
        this.resize(node);

        return node;
    }

    public void delete(Key key) {
        ensureNonEmpty();
        this.root = this.delete(this.root, key);
    }

    // delete the key-value pair with the given key rooted at node
    private Node delete(Node node, Key key) {
        if (node == null) {
            return null;
        }

        if (this.isLessThan(key, node.key)) {
            node.left = this.delete(node.left, key);
        } else if (this.isGreaterThan(key, node.key)) {
            node.right = this.delete(node.right, key);
        } else {
            if (node.right == null) {
                return node.left;
            }

            if (node.left == null) {
                return node.right;
            }

            Node temp = node;
            node = this.min(temp.right);
            node.right = this.deleteMin(temp.right);
            node.left = temp.left;
        }

        this.resize(node);

        return node;
    }

    private Node rotateRight(Node node) {
        Node temp = node.left;
        node.left = temp.right;
        temp.right = node;

        swapColors(temp, node);

        this.resize(node);
        temp.size++;

        return temp;
    }

    // make a right-leaning link lean to the left
    private Node rotateLeft(Node node) {
        Node temp = node.right;
        node.right = temp.left;
        temp.left = node;

        this.swapColors(temp, node);

        this.resize(node);
        temp.size++;

        return temp;
    }

    // flip the colors of a node and its two children
    private void flipColors(Node node) {
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

    // restore red-black tree invariant
    private Node balance(Node node) {
        if (this.isRed(node.left) && !this.isRed(node.right)) {
            if (this.isRed(node.left.right)) {
                node.left = this.rotateLeft(node.left);
            }

            if (this.isRed(node.left.left)) {
                node = this.rotateRight(node);
            }
        } else if (!this.isRed(node.left) && this.isRed(node.right)) {
            if (this.isRed(node.right.left)) {
                node.right = this.rotateRight(node.right);
            }

            if (this.isRed(node.right.right)) {
                node = this.rotateLeft(node);
            }
        } else if (this.isRed(node.left) && this.isRed(node.right)) {
            this.flipColors(node);
        }

        this.resize(node);

        return node;
    }

    public int height() {
        return this.height(this.root);
    }

    private int height(Node node) {
        if (node == null) {
            return -1;
        }

        return Math.max(this.height(node.left), this.height(node.right)) + 1;
    }

    public Key min() {
        return this.min(this.root).key;
    }

    private Node min(Node node) {
        while (node != null) {
            if (node.left == null) {
                return node;
            } else {
                node = node.left;
            }
        }

        return null;
    }

    public Key max() {
        return this.max(this.root).key;
    }

    private Node max(Node node) {
        while (node != null) {
            if (node.right == null) {
                return node;
            } else {
                node = node.right;
            }
        }

        return null;
    }

    public Key floor(Key key) {
        Node node = this.floor(this.root, key);

        return node == null ? null : node.key;
    }

    // the largest key in the subtree rooted at node less than or equal to the given key
    private Node floor(Node node, Key key) {
        if (node == null) {
            return null;
        }

        if (this.isLessThan(key, node.key)) {
            return this.floor(node.left, key);
        } else {
            if (node.right == null || this.isLessThan(key, node.right.key)) {
                return node;
            } else {
                return this.floor(node.right, key);
            }
        }
    }

    public Key ceiling(Key key) {
        Node node = this.ceiling(this.root, key);

        return node == null ? null : node.key;
    }

    // the smallest key in the subtree rooted at x greater than or equal to the given key
    private Node ceiling(Node node, Key key) {
        if (node == null) {
            return null;
        }

        if (this.isGreaterThan(key, node.key)) {
            return this.ceiling(node.right, key);
        } else {
            if (node.left == null || this.isGreaterThan(key, node.left.key)) {
                return node;
            } else {
                return this.ceiling(node.left, key);
            }
        }
    }

    public Key select(int rank) {
        return this.select(this.root, rank);
    }

    // Return key in BST rooted at node of given rank.
    // Precondition: rank is in legal range.
    private Key select(Node node, int rank) {
        if (node == null) {
            return null;
        }

        if (this.size(node.left) < rank) {
            return this.select(node.right, rank - this.size(node.left) - 1);
        } else if (this.size(node.left) > rank) {
            return this.select(node.left, rank);
        } else {
            return node.key;
        }
    }

    public int rank(Key key) {
        return this.rank(this.root, key);
    }

    // number of keys less than key in the subtree rooted at node
    private int rank(Node node, Key key) {
        if (node == null) {
            return 0;
        }

        if (isLessThan(key, node.key)) {
            return this.rank(node.left, key);
        } else if (isGreaterThan(key, node.key)) {
            return this.size(node.left) + this.rank(node.right, key) + 1;
        } else {
            return this.size(node.left);
        }
    }

    public Iterable<Key> keys() {
        List<Key> keys = new ArrayList<>();

        if (!isEmpty()) {
            Queue<Node> nodes = new ArrayDeque<>();
            nodes.add(this.root);

            while (!nodes.isEmpty()) {
                Node currentNode = nodes.poll();

                Key key = currentNode.key;
                if (key != null) {
                    keys.add(key);
                }

                if (currentNode.left != null) {
                    nodes.add(currentNode.left);
                }

                if (currentNode.right != null) {
                    nodes.add(currentNode.right);
                }
            }
        }

        return keys;
    }

    public Iterable<Key> keys(Key low, Key high) {
        Deque<Key> keys = new ArrayDeque<>();
        this.keys(this.root, keys, low, high);

        return keys;
    }

    // add the keys between lo and hi in the subtree rooted at node
    // to the queue
    private void keys(Node node, Deque<Key> queue, Key low, Key high) {
        if (node == null) {
            return;
        }
        Key key = node.key;

        if (isGreaterThan(key, low)) {
            this.keys(node.left, queue, low, high);
        }

        if (!isLessThan(key, low) && !isGreaterThan(key, high)) {
            queue.add(key);
        }

        if (isLessThan(key, high)) {
            this.keys(node.right, queue, low, high);
        }
    }

    public int size(Key low, Key high) {
        Deque<Key> deque = new ArrayDeque<>();
        this.keys(this.root, deque, low, high);

        return deque.size();
    }

    private void resize(Node node) {
        node.size = this.size(node.left) + this.size(node.right) + 1;
    }

    private boolean isGreaterThan(Key a, Key b) {
        return a.compareTo(b) > 0;
    }

    private boolean isLessThan(Key a, Key b) {
        return a.compareTo(b) < 0;
    }

    public void printPreOrder() {
        StringBuilder buffer = new StringBuilder();
        getInPreOrder(this.root, 0, buffer);
        System.out.println(buffer.toString().trim());
    }

    private void getInPreOrder(Node node, int level, StringBuilder buffer) {
        if (node == null) {
            return;
        }

        buffer.append("    ".repeat(Math.max(0, level)));

        buffer.append(node).append(System.lineSeparator());
        getInPreOrder(node.left, level + 1, buffer);
        getInPreOrder(node.right, level + 1, buffer);
    }
}