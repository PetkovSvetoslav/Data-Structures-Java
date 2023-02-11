import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedList;

public class HashTable<K, V> implements Iterable<KeyValue<K, V>> {
    private static final int INITIAL_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.80d;

    private LinkedList<KeyValue<K, V>>[] slots;
    private int count;
    private int capacity;

    public HashTable() {
        this(INITIAL_CAPACITY);
    }

    public HashTable(int capacity) {
        this.slots = createSlots(capacity);
        this.count = 0;
        this.capacity = capacity;
    }

    public void add(K key, V value) {
        this.growIfNeeded();

        int slotNumber = this.findSlotNumber(key);
        if (this.slots[slotNumber] == null) {
            this.slots[slotNumber] = new LinkedList<>();
        }

        for (KeyValue<K, V> element : this.slots[slotNumber]) {
            if (element.getKey().equals(key)) {
                throw new IllegalArgumentException("Key already exists: " + key);
            }
        }

        KeyValue<K, V> kvp = new KeyValue<>(key, value);
        this.slots[slotNumber].addLast(kvp);
        this.count++;
    }

    private int findSlotNumber(K key) {
        return Math.abs(key.hashCode()) % this.slots.length;
    }

    private void growIfNeeded() {
        if ((double) (this.size() + 1) / this.capacity > LOAD_FACTOR) {
            this.grow();
        }
    }

    private void grow() {
        HashTable<K, V> newHashTable = new HashTable<>(2 * this.slots.length);

        for (LinkedList<KeyValue<K, V>> slot : this.slots) {
            if (slot != null) {
                for (KeyValue<K, V> element : slot) {
                    newHashTable.add(element.getKey(), element.getValue());
                }
            }
        }

        this.slots = newHashTable.slots;
        this.capacity *= 2;
    }

    public int size() {
        return this.count;
    }

    public int capacity() {
        return this.capacity;
    }

    public boolean addOrReplace(K key, V value) {
        this.growIfNeeded();

        int slotNumber = this.findSlotNumber(key);
        if (this.slots[slotNumber] == null) {
            this.slots[slotNumber] = new LinkedList<>();
        }

        for (KeyValue<K, V> element : this.slots[slotNumber]) {
            if (element.getKey().equals(key)) {
                element.setValue(value);
                return true;
            }
        }

        KeyValue<K, V> kvp = new KeyValue<>(key, value);
        this.slots[slotNumber].addLast(kvp);
        this.count++;

        return false;
    }

    public V get(K key) {
        KeyValue<K, V> element = this.find(key);

        if (element == null) {
            throw new IllegalArgumentException();
        }

        return element.getValue();
    }

    public KeyValue<K, V> find(K key) {
        int slotNumber = this.findSlotNumber(key);
        LinkedList<KeyValue<K, V>> elements = this.slots[slotNumber];

        if (elements != null) {
            for (KeyValue<K, V> element : elements) {
                if (element.getKey().equals(key)) {
                    return element;
                }
            }
        }

        return null;
    }

    public boolean containsKey(K key) {
        return find(key) != null;
    }

    public boolean remove(K key) {
        int slotNumber = findSlotNumber(key);
        LinkedList<KeyValue<K, V>> slot = this.slots[slotNumber];

        if (slot != null) {
            for (int i = 0; i < slot.size(); i++) {
                if (slot.get(i).getKey().equals(key)) {
                    slot.remove(i);
                    this.count--;

                    return true;
                }
            }
        }

        return false;
    }

    public void clear() {
        this.slots = createSlots(INITIAL_CAPACITY);
        this.count = 0;
        this.capacity = INITIAL_CAPACITY;
    }

    public Iterable<K> keys() {
        LinkedList<K> keys = new LinkedList<>();
        this.forEach(e -> keys.add(e.getKey()));

        return keys;
    }

    public Iterable<V> values() {
        LinkedList<V> values = new LinkedList<>();
        this.forEach(e -> values.add(e.getValue()));

        return values;
    }

    @Override
    public Iterator<KeyValue<K, V>> iterator() {
        return new HashTableIterator();
    }

    private class HashTableIterator implements Iterator<KeyValue<K, V>> {
        int counter = 0;
        int pointer = 0;
        int innerPointer = 0;
        LinkedList<KeyValue<K, V>> slot = null;

        @Override
        public boolean hasNext() {
            return this.counter < size();
        }

        @Override
        public KeyValue<K, V> next() {
            while (this.slot == null) {
                this.slot = slots[this.pointer++];
            }

            KeyValue<K, V> result = this.slot.get(this.innerPointer++);

            if (this.innerPointer >= this.slot.size()) {
                this.slot = null;
                this.innerPointer = 0;
            }

            this.counter++;
            return result;
        }
    }

    @SuppressWarnings("unchecked")
    private LinkedList<KeyValue<K, V>>[] createSlots(int capacity) {
        return (LinkedList<KeyValue<K, V>>[]) Array.newInstance(LinkedList.class, capacity);
    }
}