public class TwoThreeTree<K extends Comparable<K>> {
    private TreeNode<K> root;

    public static class TreeNode<K> {
        private K leftKey;
        private K rightKey;

        private TreeNode<K> leftChild;
        private TreeNode<K> middleChild;
        private TreeNode<K> rightChild;

        private TreeNode(K key) {
            this.leftKey = key;
        }

        private TreeNode(K key, K leftChild, K rightChild) {
            this(key);
            this.leftChild = new TreeNode<>(leftChild);
            this.rightChild = new TreeNode<>(rightChild);
        }

        private TreeNode(K key, TreeNode<K> leftChild, TreeNode<K> rightChild) {
            this(key);
            this.leftChild = leftChild;
            this.rightChild = rightChild;
        }

        boolean isThreeNode() {
            return rightKey != null;
        }

        boolean isTwoNode() {
            return rightKey == null;
        }

        boolean isLeaf() {
            return this.leftChild == null && this.middleChild == null && this.rightChild == null;
        }
    }

    public void insert(K key) {
        if (this.root == null) {
            this.root = new TreeNode<>(key);
        } else {

            TreeNode<K> node = this.insert(key, this.root);

            if (node != null) {
                this.root = node;
            }
        }
    }

    private TreeNode<K> insert(K key, TreeNode<K> node) {
        if (node.isLeaf()) {
            if (node.isTwoNode()) {
                if (isGreaterThan(key, node.leftKey)) {
                    node.rightKey = key;
                } else {
                    node.rightKey = node.leftKey;
                    node.leftKey = key;
                }
            } else {
                return splitLeaf(node, key);
            }
        } else {
            TreeNode<K> restructuredNode;

            if (isLessThan(key, node.leftKey)) {
                restructuredNode = insert(key, node.leftChild);
            } else if (node.isThreeNode() && isLessThan(key, node.rightKey)) {
                restructuredNode = insert(key, node.middleChild);
            } else {
                restructuredNode = insert(key, node.rightChild);
            }

            if (restructuredNode != null) {
                return splitNode(node, restructuredNode);
            }
        }

        return null;
    }

    private TreeNode<K> splitNode(TreeNode<K> node, TreeNode<K> restructuredNode) {
        if (node.isTwoNode()) {
            if (isLessThan(restructuredNode.leftKey, node.leftKey)) {
                node.rightKey = node.leftKey;
                node.leftKey = restructuredNode.leftKey;

                node.leftChild = restructuredNode.leftChild;
                node.middleChild = restructuredNode.rightChild;
            } else {
                node.rightKey = restructuredNode.leftKey;

                node.middleChild = restructuredNode.leftChild;
                node.rightChild = restructuredNode.rightChild;
            }
        } else {
            TreeNode<K> newNode;

            if (isLessThan(restructuredNode.leftKey, node.leftKey)) {
                newNode = new TreeNode<>(node.leftKey);

                newNode.leftChild = new TreeNode<>(restructuredNode.leftKey);
                newNode.rightChild = new TreeNode<>(restructuredNode.rightKey);

                newNode.rightChild.leftChild = node.middleChild;
                newNode.rightChild.rightChild = node.rightChild;
            } else if (isGreaterThan(restructuredNode.leftKey, node.rightKey)) {
                newNode = new TreeNode<>(node.rightKey);

                newNode.leftChild = new TreeNode<>(node.leftKey);
                newNode.rightChild = restructuredNode;

                newNode.leftChild.leftChild = node.leftChild;
                newNode.leftChild.rightChild = node.middleChild;
            } else {
                newNode = new TreeNode<>(restructuredNode.leftKey);

                newNode.leftChild = new TreeNode<>(node.leftKey);
                newNode.rightChild = new TreeNode<>(node.rightKey);

                newNode.leftChild.leftChild = node.leftChild;
                newNode.leftChild.rightChild = restructuredNode.leftChild;

                newNode.rightChild.rightChild = node.rightChild;
                newNode.rightChild.leftChild = restructuredNode.rightChild;
            }
            return newNode;
        }
        return null;
    }

    private TreeNode<K> splitLeaf(TreeNode<K> node, K key) {
        K left;
        K mid;
        K right;

        if (isLessThan(key, node.leftKey)) {
            left = key;
            mid = node.leftKey;
            right = node.rightKey;
        } else if (isGreaterThan(key, node.rightKey)) {
            left = node.leftKey;
            mid = node.rightKey;
            right = key;
        } else {
            left = node.leftKey;
            mid = key;
            right = node.rightKey;
        }

        return new TreeNode<>(mid, left, right);
    }

    private TreeNode<K> splitNode(TreeNode<K> node, K key) {

        return null;
    }

    public String getAsString() {
        StringBuilder out = new StringBuilder();
        recursivePrint(this.root, out);
        return out.toString().trim();
    }

    private void recursivePrint(TreeNode<K> node, StringBuilder out) {
        if (node == null) {
            return;
        }
        if (node.leftKey != null) {
            out.append(node.leftKey)
                    .append(" ");
        }
        if (node.rightKey != null) {
            out.append(node.rightKey).append(System.lineSeparator());
        } else {
            out.append(System.lineSeparator());
        }
        if (node.isTwoNode()) {
            recursivePrint(node.leftChild, out);
            recursivePrint(node.rightChild, out);
        } else if (node.isThreeNode()) {
            recursivePrint(node.leftChild, out);
            recursivePrint(node.middleChild, out);
            recursivePrint(node.rightChild, out);
        }
    }

    private TreeNode<K> findNode(K key) {
        if (this.root == null) {
            return null;
        }

        return findNode(key, this.root);
    }

    private TreeNode<K> findNode(K key, TreeNode<K> node) {
        if (node.isLeaf()) {
            return node;
        }

        if (isLessThan(key, node.leftKey)) {
            return findNode(key, node.leftChild);
        } else if (node.rightKey != null && isLessThan(node.rightKey, key)) {
            return findNode(key, node.rightChild);
        } else {
            return findNode(key, node.middleChild);
        }
    }

    private boolean isLessThan(K a, K b) {
        return a.compareTo(b) < 0;
    }

    private boolean isGreaterThan(K a, K b) {
        return a.compareTo(b) > 0;
    }

    private boolean isBetween(K element, K boundA, K boundB) {
        return isGreaterThan(element, boundA) && isLessThan(element, boundB)
                || isLessThan(element, boundA) && isGreaterThan(element, boundB);
    }
}
