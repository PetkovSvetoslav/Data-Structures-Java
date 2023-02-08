package interfaces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface AbstractBinarySearchTree<E extends Comparable<E>> {
    class Node<E> {
        public E value;
        public Node<E> leftChild;
        public Node<E> rightChild;

        public Node(E value) {
            this.value = value;
        }

        public Node(E value, Node<E> leftChild, Node<E> rightChild) {
            this.value = value;
            this.leftChild = leftChild;
            this.rightChild = rightChild;
        }

        public E getValue() {
            return value;
        }

        public Node<E> getLeft() {
            return leftChild;
        }

        public Node<E> getRight() {
            return rightChild;
        }
    }

    void insert(E element);

    boolean contains(E element);

    AbstractBinarySearchTree<E> search(E element);

    Node<E> getRoot();

    Node<E> getLeft();

    Node<E> getRight();

    E getValue();

    void print();
}