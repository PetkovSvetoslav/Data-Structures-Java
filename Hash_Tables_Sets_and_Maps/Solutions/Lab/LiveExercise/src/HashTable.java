import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;

public class HashTable<K, V> implements Iterable<KeyValue<K, V>> {
    private static final int INITIAL_CAPACITY = 16;
    private static final double MAX_LOAD_FACTOR = 0.7;

    private LinkedList<KeyValue<K, V>>[] table;
    private int size;
    private int capacity;

    public HashTable() {
        this(INITIAL_CAPACITY);
    }

    private HashTable(int capacity) {
        this.table = createTable(capacity);
        this.size = 0;
        this.capacity = capacity;
    }

    public void put(K key, V value) {
        int index = convertToIndex(key);

        if (this.table[index] == null) {
            this.table[index] = new LinkedList<>();
        }

        if (!set(this.table[index], key, value)) {
            this.table[index].add(new KeyValue<>(key, value));
        }

        this.size++;

        if (this.size >= this.capacity * MAX_LOAD_FACTOR) {
            this.doubleTheCapacity();
        }
    }

    private boolean set(LinkedList<KeyValue<K, V>> chain, K key, V value) {
        for (KeyValue<K, V> element : chain) {
            if (element.getKey().equals(key)) {
                element.setValue(value);
                return true;
            }
        }

        return false;
    }


    private void doubleTheCapacity() {
        HashTable<K, V> newHashTable = new HashTable<>(this.capacity *= 2);

        for (KeyValue<K, V> element : this) {
            newHashTable.put(element.getKey(), element.getValue());
        }

        this.table = newHashTable.table;
    }

    public V get(K key) {
        int index = convertToIndex(key);
        LinkedList<KeyValue<K, V>> chain = this.table[index];

        if (chain != null) {
            for (KeyValue<K, V> element : chain) {
                if (element.getKey().equals(key)) {
                    return element.getValue();
                }
            }
        }

        return null;
    }

    private int convertToIndex(K key) {
        int hashCode = key.hashCode();

        return Math.abs(hashCode) % table.length;
    }

    @SuppressWarnings("unchecked")
    private LinkedList<KeyValue<K, V>>[] createTable(int size) {
        return (LinkedList<KeyValue<K, V>>[]) Array.newInstance(LinkedList.class, size);
    }

    private class HashIterator implements Iterator<KeyValue<K, V>> {
        int counter = 0;
        int pointer = 0;
        int innerPointer = 0;
        LinkedList<KeyValue<K, V>> chain = null;

        @Override
        public boolean hasNext() {
            return this.counter < size;
        }

        @Override
        public KeyValue<K, V> next() {
            while (this.chain == null) {
                this.chain = table[this.pointer++];
            }

            KeyValue<K, V> result = this.chain.get(this.innerPointer++);

            if (this.innerPointer >= this.chain.size()) {
                this.chain = null;
                this.innerPointer = 0;
            }

            this.counter++;
            return result;
        }
    }

    @Override
    public Iterator<KeyValue<K, V>> iterator() {
        return new HashIterator();
    }

    @Override
    public void forEach(Consumer<? super KeyValue<K, V>> action) {
        Iterable.super.forEach(action);
    }
}