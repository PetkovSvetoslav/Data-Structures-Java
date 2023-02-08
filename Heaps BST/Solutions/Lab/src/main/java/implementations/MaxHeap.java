package implementations;

import interfaces.Heap;

import java.util.ArrayList;
import java.util.List;

public class MaxHeap<E extends Comparable<E>> implements Heap<E> {
    private final List<E> elements = new ArrayList<>();

    @Override
    public int size() {
        return elements.size();
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

    private boolean isBiggerThan(int firstIndex, int secondIndex) {
        return this.elements.get(firstIndex).compareTo(this.elements.get(secondIndex)) > 0;
    }

    private void swap(int indexA, int indexB) {
        E oldElementA = this.elements.get(indexA);
        this.elements.set(indexA, this.elements.get(indexB));
        this.elements.set(indexB, oldElementA);
    }

    @Override
    public E peek() {
        return this.elements.get(0);
    }
}