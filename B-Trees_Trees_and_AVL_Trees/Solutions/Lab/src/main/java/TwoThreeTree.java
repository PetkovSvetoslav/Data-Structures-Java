import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TwoThreeTree<K extends Comparable<K>> {

    private static <K extends Comparable<K>> boolean isLessThan(K a, K b) {
        return a.compareTo(b) < 0;
    }

    static class Node23<K extends Comparable<K>> {
        Node23<K> parent;
        private final K key;
        List<K> keys;
        List<Node23<K>> subNodes;

        public Node23(Node23<K> parent, K key) {
            this(parent, key, new ArrayList<>());
        }

        public Node23(Node23<K> parent, K key, List<Node23<K>> subNodes) {
            this.parent = parent;
            this.key = key;
            this.keys = new ArrayList<>();

            this.keys.add(key);
            this.subNodes = new ArrayList<>(subNodes);

            for (Node23<K> subNode : this.subNodes) {
                subNode.parent = this;
            }
        }

        @Override
        public String toString() {
            return "{" +
                    this.keys.stream()
                            .map(Object::toString)
                            .collect(Collectors.joining(","))
                    + "}";
        }

        boolean contains(K key) {
            return keys.contains(key);
        }

        public Node23<K> insert(K key) {
            if (!subNodes.isEmpty()) {
                throw new IllegalStateException("Cannot insert into non-leaf node " + this);
            }

            keys.add(getInsertionIndex(key), key);

            Node23<K> insertionNode = this;

            while (insertionNode.keys.size() == 3) {

                Node23<K> leftPart = new Node23<>(insertionNode.parent, insertionNode.keys.get(0),
                        !insertionNode.subNodes.isEmpty()
                                ? insertionNode.subNodes.subList(0, 2)
                                : new ArrayList<>());
                Node23<K> rightPart = new Node23<>(insertionNode.parent, insertionNode.keys.get(2),
                        !insertionNode.subNodes.isEmpty()
                                ? insertionNode.subNodes.subList(2, 4)
                                : new ArrayList<>());

                K median = insertionNode.keys.get(1);

                if (insertionNode.parent != null) {

                    insertionNode = insertionNode.parent;
                    int insertionIndex = insertionNode.getInsertionIndex(key);
                    insertionNode.keys.add(insertionIndex, key);
                    insertionNode.subNodes.add(insertionIndex, leftPart);
                    insertionNode.subNodes.set(insertionIndex + 1, rightPart);

                } else {

                    insertionNode = new Node23<>(null, median, Arrays.asList(leftPart, rightPart));

                }
            }

            return insertionNode;
        }

        private boolean isLessThan(K a, K b) {
            return a.compareTo(b) < 0;
        }

        private int getInsertionIndex(K key) {
            for (int i = 0; i < keys.size(); i++) {
                if (isLessThan(key, keys.get(i))) {
                    return i;
                }
            }

            return keys.size();
        }
    }

    private Node23<K> root;

    public void add(K key) {
        if (this.root == null) {
            this.root = new Node23<>(null, key, Collections.emptyList());
        } else {
            Node23<K> insertionNode = findNode(key);
            Node23<K> lastInsertionNode = insertionNode.insert(key);

            if (lastInsertionNode.parent == null) {
                this.root = lastInsertionNode;
            }
        }
    }

    /**
     *
     * Looking for a leaf for the key
     *
     * @param key
     * @return Node where we can add the key
     */
    private Node23<K> findNode(K key) {
        if (root == null) {
            return null;
        }

        return findNode(root, key);
    }

    private Node23<K> findNode(Node23<K> node, K key) {
        if (node.subNodes.isEmpty()) {
            return node;
        }

        for (int i = 0; i < node.keys.size(); i++) {
            if (isLessThan(key, node.keys.get(i))) {
                return findNode(node.subNodes.get(i), key);
            }
        }

        return findNode(node.subNodes.get(node.subNodes.size() - 1), key);
    }

    public String toStringPreOrder() {
        StringBuilder builder = new StringBuilder();

        toStringPreOrder(root, 0, builder);

        return builder.toString();
    }

    public void toStringPreOrder(Node23<K> node, int level, StringBuilder output) {
        if (node == null) {
            return;
        }

        output.append("    ".repeat(Math.max(0, level)));

        output.append(node);
        output.append("\n");
        for (Node23<K> subNode : node.subNodes) {
            toStringPreOrder(subNode, level + 1, output);
        }
    }
}