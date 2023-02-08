package implementations;

import interfaces.AbstractQueue;

import java.util.ArrayList;
import java.util.List;

public class PriorityQueue<E extends Comparable<E>> implements AbstractQueue<E> {
    private final List<E> elements = new ArrayList<>();

    @Override
    public int size() {
        return this.elements.size();
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public void add(E element) {
        this.elements.add(element);

        swim(this.size() - 1);
    }

    private void swim(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;

            if (isBiggerThan(index, parentIndex)) {
                swap(index, parentIndex);
                index = parentIndex;
            } else {
                break;
            }
        }
    }

    @Override
    public E peek() {
        return this.elements.get(0);
    }

    @Override
    public E poll() {
        ensureNonEmpty();

        E poppedElement = this.elements.get(0);
        this.elements.set(0, this.elements.get(this.size() - 1));
        this.elements.remove(this.size() - 1);

        sink(0);
        return poppedElement;
    }

    private void sink(int index) {
        while (isValidIndex(index)) {
            int firstChildIndex = index * 2 + 1;
            int secondChildIndex = index * 2 + 2;

            if (isValidIndex(firstChildIndex)) {

                int maxChildIndex;

                if (isValidIndex(secondChildIndex) && isBiggerThan(secondChildIndex, firstChildIndex)) {
                    maxChildIndex = secondChildIndex;
                } else {
                    maxChildIndex = firstChildIndex;
                }

                if (isBiggerThan(maxChildIndex, index)) {
                    swap(index, maxChildIndex);
                    index = maxChildIndex;
                } else {
                    break;
                }
            } else {
                break;
            }
        }
    }

    private void ensureNonEmpty() {
        if (this.isEmpty()) {
            throw new IllegalStateException("Heap is empty upon peek/poll attempt");
        }
    }

    private boolean isBiggerThan(int firstIndex, int secondIndex) {
        return this.elements.get(firstIndex).compareTo(this.elements.get(secondIndex)) > 0;
    }

    private void swap(int indexA, int indexB) {
        E oldElementA = this.elements.get(indexA);
        this.elements.set(indexA, this.elements.get(indexB));
        this.elements.set(indexB, oldElementA);
    }

    private boolean isValidIndex(int index) {
        return 0 <= index && index < this.size();
    }
}