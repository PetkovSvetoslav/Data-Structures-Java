package solutions;

import interfaces.Decrease;
import interfaces.Heap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MinHeap<E extends Comparable<E> & Decrease<E>> implements Heap<E> {
    private List<E> elements;

    public MinHeap() {
        this.elements = new ArrayList<>();
    }

    @Override
    public int size() {
        return this.elements.size();
    }

    @Override
    public void add(E element) {
        this.elements.add(element);
        heapifyUp(this.size() - 1);
    }

    private void heapifyUp(int index) {
        while (index > 0) {
            int parentIndex = getParentIndex(index);

            if (isLessThan(index, parentIndex)) {
                Collections.swap(elements, index, parentIndex);
                index = parentIndex;
            } else {
                break;
            }
        }
    }

    @Override
    public E peek() {
        ensureNonEmpty();
        return this.elements.get(0);
    }

    @Override
    public E poll() {
        ensureNonEmpty();

        Collections.swap(this.elements, 0, this.size() - 1);

        E removedElement = this.elements.remove(this.size() - 1);

        heapifyDown(0);

        return removedElement;
    }

    private void heapifyDown(int index) {
        int leftChildIndex = getLeftChildIndex(index);

        while (leftChildIndex < this.size()) {
            int swapIndex = leftChildIndex;
            int rightChildIndex = getRightChildIndex(index);

            if (rightChildIndex < this.size()) {
                swapIndex = getTheIndexOfSmallerElement(leftChildIndex, rightChildIndex);
            }

            if (isLessThan(swapIndex, index)) {
                Collections.swap(this.elements, index, swapIndex);
                index = swapIndex;
            } else {
                break;
            }
        }
    }

    private boolean isLessThan(int firstIndex, int secondIndex) {
        return this.elements.get(firstIndex).compareTo(this.elements.get(secondIndex)) < 0;
    }

    private int getTheIndexOfSmallerElement(int first, int second) {
        return this.elements.get(first).compareTo(this.elements.get(second)) < 0
                ? first
                : second;
    }

    private int getParentIndex(int index) {
        return (index - 1) / 2;
    }

    private int getLeftChildIndex(int index) {
        return index * 2 + 1;
    }

    private int getRightChildIndex(int index) {
        return getLeftChildIndex(index) + 1;
    }

    private void ensureNonEmpty() {
        if (this.size() == 0) {
            throw new IllegalStateException("Heap is empty upon peak/poll attempt");
        }
    }

    @Override
    public void decrease(E element) {
        int index = this.elements.indexOf(element);

        if (index >= 0) {
            this.elements.get(index).decrease();
            heapifyUp(index);
        }
    }
}