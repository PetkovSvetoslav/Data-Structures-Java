package implementations;

import interfaces.AbstractBinarySearchTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BinarySearchTree<E extends Comparable<E>> implements AbstractBinarySearchTree<E> {
    private Node<E> root;

    public BinarySearchTree() {
    }

    private BinarySearchTree(Node<E> node) {
        this.root = node;
    }

    @Override
    public void insert(E element) {
        Node<E> newNode = new Node<>(element, null, null);

        if (this.root == null) {
            this.root = newNode;
        }

        Node<E> currentNode = this.root;
        while (true) {
            int compare = element.compareTo(currentNode.value);
            if (compare < 0) {
                if (currentNode.leftChild == null) {
                    currentNode.leftChild = newNode;
                    return;
                } else {
                    currentNode = currentNode.leftChild;
                }
            } else if (compare > 0) {
                if (currentNode.rightChild == null) {
                    currentNode.rightChild = newNode;
                    return;
                } else {
                    currentNode = currentNode.rightChild;
                }
            } else {
                return;
            }
        }
    }

    @Override
    public boolean contains(E element) {
        Node<E> node = root;

        while (node != null) {
            int compare = element.compareTo(node.value);

            if (compare == 0) {
                return true;
            }

            if (compare > 0) {
                node = node.rightChild;
            } else {
                node = node.leftChild;
            }
        }
        return false;
    }

    @Override
    public AbstractBinarySearchTree<E> search(E element) {
        Node<E> node = root;

        while (node != null) {
            int compare = element.compareTo(node.value);

            if (compare == 0) {
                break;
            }

            if (compare > 0) {
                node = node.rightChild;
            } else {
                node = node.leftChild;
            }
        }

        return new BinarySearchTree<>(copy(node));
    }

    private Node<E> copy(Node<E> node) {
        if (node == null) {
            return null;
        }

        return new Node<E>(
                node.value,
                copy(node.leftChild),
                copy(node.rightChild)
        );
    }

    @Override
    public Node<E> getRoot() {
        return this.root;
    }

    @Override
    public Node<E> getLeft() {
        return this.root.leftChild;
    }

    @Override
    public Node<E> getRight() {
        return this.root.rightChild;
    }

    @Override
    public E getValue() {
        return this.root.value;
    }

    @Override
    public void print() {
        BTreePrinter.printNode(this.root);
    }

    private static class BTreePrinter {

        public static <K extends Comparable<K>> void printNode(Node<K> root) {
            int maxLevel = BTreePrinter.maxLevel(root);
            StringBuilder buffer = new StringBuilder();

            getNodeInternal(Collections.singletonList(root), 1, maxLevel, buffer);
            System.out.println(buffer);
        }

        private static <K extends Comparable<K>> void getNodeInternal(List<Node<K>> nodes, int level, int maxLevel, StringBuilder buffer) {
            if (nodes.isEmpty() || BTreePrinter.isAllElementsNull(nodes)) {
                return;
            }

            int floor = maxLevel - level;
            int edgeLines = (int) Math.pow(2, (Math.max(floor - 1, 0)));
            int firstSpaces = (int) Math.pow(2, (floor)) - 1;
            int betweenSpaces = (int) Math.pow(2, (floor + 1)) - 1;

            BTreePrinter.printWhitespaces(firstSpaces, buffer);

            List<Node<K>> newNodes = new ArrayList<>();
            for (Node<K> node : nodes) {
                if (node != null) {
                    buffer.append(node.getValue());
                    newNodes.add(node.getLeft());
                    newNodes.add(node.getRight());
                } else {
                    newNodes.add(null);
                    newNodes.add(null);
                    buffer.append(" ");
                }

                BTreePrinter.printWhitespaces(betweenSpaces, buffer);
            }
            buffer.append(System.lineSeparator());

            for (int i = 1; i <= edgeLines; i++) {
                for (Node<K> node : nodes) {
                    BTreePrinter.printWhitespaces(firstSpaces - i, buffer);
                    if (node == null) {
                        BTreePrinter.printWhitespaces(edgeLines + edgeLines + i + 1, buffer);
                        continue;
                    }

                    if (node.getLeft() != null) {
                        buffer.append("/");
                    } else {
                        BTreePrinter.printWhitespaces(1, buffer);
                    }
                    BTreePrinter.printWhitespaces(i + i - 1, buffer);

                    if (node.getRight() != null) {
                        buffer.append("\\");
                    } else {
                        BTreePrinter.printWhitespaces(1, buffer);
                    }
                    BTreePrinter.printWhitespaces(edgeLines + edgeLines - i, buffer);
                }
                buffer.append(System.lineSeparator());
            }
            getNodeInternal(newNodes, level + 1, maxLevel, buffer);
        }

        private static void printWhitespaces(int count, StringBuilder buffer) {
            buffer.append(" ".repeat(Math.max(0, count)));
        }

        private static <K extends Comparable<K>> int maxLevel(Node<K> node) {
            if (node == null) {
                return 0;
            }
            return Math.max(BTreePrinter.maxLevel(node.getLeft()), BTreePrinter.maxLevel(node.getRight())) + 1;
        }

        private static <K> boolean isAllElementsNull(List<K> list) {
            for (Object object : list) {
                if (object != null)
                    return false;
            }

            return true;
        }
    }
}