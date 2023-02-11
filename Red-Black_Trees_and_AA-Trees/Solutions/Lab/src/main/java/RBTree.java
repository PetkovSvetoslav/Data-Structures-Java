public class RBTree<K extends Comparable<K>> {

    private enum Color {
        RED,
        BLACK
    }

    private static class Node<K> {
        K key;
        Node<K> parent;
        Node<K> left;
        Node<K> right;
        Color color;

        Node(K key, Node<K> parent) {
            this.key = key;
            this.parent = parent;
            this.color = parent == null
                    ? Color.BLACK
                    : Color.RED;
        }

        Node(K key) {
            this(key, null);
        }

        void setLeft(Node<K> node) {
            this.left = node;

            if (node != null) {
                node.parent = this;
            }
        }

        void setRight(Node<K> node) {
            this.right = node;

            if (node != null) {
                node.parent = this;
            }
        }

        Color getUncleColor() {
            Node<K> uncle = getUncle();

            if (uncle != null) {
                return uncle.color;
            }

            return Color.BLACK;
        }

        Node<K> getUncle() {
            Node<K> grandparent = getGrandParent();

            if (grandparent == null) {
                return null;
            }

            if (this.parent == grandparent.left) {
                return grandparent.right;
            } else {
                return grandparent.left;
            }
        }

        Node<K> getGrandParent() {
            if (this.parent == null) {
                return null;
            }

            return this.parent.parent;
        }

        @Override
        public String toString() {
            return this.color.equals(Color.RED) ? "R" : "B"
                    + "(" + this.key + ")";
        }
    }

    private Node<K> root;

    public void add(K key) {
        if (this.root == null) {
            this.root = new Node<>(key);
        } else {
            Node<K> parent = this.root;

            Node<K> redNode = null;
            while (redNode == null) {
                if (isLessThan(key, parent.key)) {
                    if (parent.left == null) {
                        redNode = new Node<>(key, parent);
                        parent.left = redNode;
                    } else {
                        parent = parent.left;
                    }
                } else {
                    if (parent.right == null) {
                        redNode = new Node<>(key, parent);
                        parent.right = redNode;
                    } else {
                        parent = parent.right;
                    }
                }
            }
            ensureBalance(redNode);
        }
    }

    private void ensureBalance(Node<K> redNode) {
        if (redNode.parent.color == Color.RED) {
            while (redNode != null
                    && redNode.parent != null
                    && redNode.parent.color == Color.RED) {

                boolean uncleIsBlack = redNode.getUncleColor() == Color.BLACK;
                if (uncleIsBlack) {
                    // uncle is black -> rotations
                    Node<K> grandparent = redNode.getGrandParent();
                    if (grandparent != null && grandparent.left == redNode.parent) {
                        if (redNode.parent.left != redNode) {
                            rotateLeft(redNode.parent);
                        }
                        fixRedLeftLeft(grandparent);
                    } else {
                        if (redNode.parent.left != redNode) {
                            rotateRight(redNode.parent);
                        }
                        fixRedRightRight(grandparent);
                    }
                    break;
                } else {
                    // uncle is red -> changing the colors of the grandparent and his two children (parent & uncle)
                    Node<K> grandparent = redNode.getGrandParent();
                    Node<K> parent = redNode.parent;
                    Node<K> uncle = redNode.getUncle();

                    if (grandparent != null) {
                        grandparent.color = Color.RED;
                    }

                    parent.color = Color.BLACK;
                    uncle.color = Color.BLACK;
                    redNode = grandparent;
                }
            }
            this.root.color = Color.BLACK;
        }
    }

    private void rotateRight(Node<K> node) {
        if (node.parent != null) {
            if (node.parent.left == node) {
                node.parent.setLeft(node.left);
            } else {
                node.parent.setRight(node.left);
            }
        } else {
            node.left.parent = null;
            this.root = node.left;
        }

        Node<K> leftRight = node.left.right;
        node.left.setRight(node);
        node.setLeft(leftRight);
    }

    private void rotateLeft(Node<K> node) {
        if (node.parent != null) {
            if (node.parent.left == node) {
                node.parent.setLeft(node.right);
            } else {
                node.parent.setRight(node.right);
            }
        } else {
            node.right.parent = null;
            this.root = node.right;
        }

        Node<K> rightLeft = node.right.left;
        node.right.setLeft(node);
        node.setRight(rightLeft);
    }

    private void fixRedRightRight(Node<K> grandparent) {
        swapColors(grandparent, grandparent.right);
        rotateLeft(grandparent);
    }

    private void fixRedLeftLeft(Node<K> grandparent) {
        swapColors(grandparent, grandparent.left);
        rotateRight(grandparent);
    }

    private void swapColors(Node<K> a, Node<K> b) {
        Color aColor = a.color;
        a.color = b.color;
        b.color = aColor;
    }

    private boolean isLessThan(K first, K second) {
        return first.compareTo(second) < 0;
    }

    public void printPreOrder() {
        StringBuilder buffer = new StringBuilder();
        getInPreOrder(this.root, 0, buffer);
        System.out.println(buffer);
    }

    private void getInPreOrder(Node<K> node, int level, StringBuilder buffer) {
        if (node == null) {
            return;
        }

        buffer.append("    ".repeat(Math.max(0, level)));

        buffer.append(node).append(System.lineSeparator());
        getInPreOrder(node.left, level + 1, buffer);
        getInPreOrder(node.right, level + 1, buffer);
    }
}