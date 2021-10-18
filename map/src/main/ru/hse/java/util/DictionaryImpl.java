package ru.hse.java.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DictionaryImpl<K, V> implements Dictionary<K, V> {
    private int size;
    private int capacity;
    private int preCapacity;
    private List<MyLinkedList<AbstractMap.SimpleEntry<K, V>>> data;
    private Set<K> keySet;
    private Collection<V> values;
    private Set<Entry<K, V>> entrySet;
    private final double loadFactor;
    private final int resizeCoeff;
    private final int outside;

    public DictionaryImpl() {
        this(0.75, 2, 10);
    }

    public DictionaryImpl(double loadFactor, int resizeCoeff, int outside) {
        this.loadFactor = loadFactor;
        this.resizeCoeff = resizeCoeff;
        this.outside = outside;
        preCapacity = 1;
        size = 0;
        capacity = updateCapacity(preCapacity);
        data = genArrayList(capacity);
    }

    private static int updateCapacity(int preCap) {
        return 6 * preCap + 1;
    }

    private List<MyLinkedList<AbstractMap.SimpleEntry<K, V>>> genArrayList(int newCapacity) {
        List<MyLinkedList<AbstractMap.SimpleEntry<K, V>>> arrayList = new ArrayList<>(newCapacity);
        for (int i = 0; i < newCapacity; i++) {
            arrayList.add(new MyLinkedList<>());
        }
        return arrayList;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        int ind = Math.abs(key.hashCode() % capacity);
        for (Entry<K, V> element : data.get(ind)) {
            if (key.equals(element.getKey())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        for (MyLinkedList<AbstractMap.SimpleEntry<K, V>> list : data) {
            for (Entry<K, V> element : list) {
                if (value.equals(element.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    private MyLinkedList<AbstractMap.SimpleEntry<K, V>> getDataList(Object key) {
        return data.get(Math.abs(key.hashCode() % capacity));
    }

    @Override
    public V get(Object key) {
        MyLinkedList<AbstractMap.SimpleEntry<K, V>> list = getDataList(key);
        for (Entry<K, V> element : list) {
            if (key.equals(element.getKey())) {
                return element.getValue();
            }
        }
        return null;
    }

    @Override
    public V put(@NotNull K key, V value) {
        MyLinkedList<AbstractMap.SimpleEntry<K, V>> list = getDataList(key);
        for (Entry<K, V> element : list) {
            if (key.equals(element.getKey())) {
                V oldValue = element.getValue();
                element.setValue(value);
                return oldValue;
            }
        }
        list.add(new AbstractMap.SimpleEntry<>(key, value));
        size++;
        if (size > capacity * loadFactor + outside) {
            rehashing(true);
        }
        return null;
    }

    @Override
    public V remove(Object key) {
        MyLinkedList<AbstractMap.SimpleEntry<K, V>> list = getDataList(key);
        for (Iterator<AbstractMap.SimpleEntry<K, V>> it = list.iterator(); it.hasNext(); ) {
            AbstractMap.SimpleEntry<K, V> element = it.next();
            if (key.equals(element.getKey())) {
                V oldValue = element.getValue();
                it.remove();
                size--;
                if (size < capacity * loadFactor / resizeCoeff - outside) {
                    rehashing(false);
                }
                return oldValue;
            }
        }
        return null;
    }

    private void rehashing(boolean more) {
        if (more) {
            preCapacity *= resizeCoeff;
        } else {
            preCapacity /= resizeCoeff;
        }
        capacity = updateCapacity(preCapacity);
        List<MyLinkedList<AbstractMap.SimpleEntry<K, V>>> oldData = data;
        data = genArrayList(capacity);
        size = 0;
        for (MyLinkedList<AbstractMap.SimpleEntry<K, V>> list : oldData) {
            for (Entry<K, V> element : list) {
                put(element.getKey(), element.getValue());
            }
        }
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> element : m.entrySet()) {
            put(element.getKey(), element.getValue());
        }
    }

    @Override
    public void clear() {
        data.clear();
        size = 0;
        preCapacity = 1;
        capacity = updateCapacity(preCapacity);
        data = genArrayList(capacity);
    }

    @Override
    public @NotNull Set<K> keySet() {
        if (keySet == null) {
            keySet = new AbstractSet<>() {
                @Override
                public @NotNull Iterator<K> iterator() {
                    return new SetIterator(new EntrySetIterator());
                }

                @Override
                public int size() {
                    return size;
                }
            };
        }
        return keySet;
    }


    private class SetIterator implements Iterator<K> {
        private final Iterator<Entry<K, V>> delegateIterator;

        private SetIterator(Iterator<Entry<K, V>> delegateIterator) {
            this.delegateIterator = delegateIterator;
        }

        @Override
        public boolean hasNext() {
            return delegateIterator.hasNext();
        }

        @Override
        public K next() {
            return delegateIterator.next().getKey();
        }

        @Override
        public void remove() {
            delegateIterator.remove();
        }
    }


    @Override
    public @NotNull Collection<V> values() {
        if (values == null) {
            values = new AbstractCollection<>() {
                @Override
                public @NotNull Iterator<V> iterator() {
                    return new CollectionIterator(new EntrySetIterator());
                }

                @Override
                public int size() {
                    return size;
                }
            };
        }
        return values;
    }

    private class CollectionIterator implements Iterator<V> {
        private final Iterator<Entry<K, V>> delegateIterator;

        private CollectionIterator(Iterator<Entry<K, V>> delegateIterator) {
            this.delegateIterator = delegateIterator;
        }

        @Override
        public boolean hasNext() {
            return delegateIterator.hasNext();
        }

        @Override
        public V next() {
            return delegateIterator.next().getValue();
        }

        @Override
        public void remove() {
            delegateIterator.remove();
        }
    }

    @Override
    public @NotNull Set<Entry<K, V>> entrySet() {
        if (entrySet == null) {
            entrySet = new AbstractSet<>() {
                @Override
                public @NotNull Iterator<Entry<K, V>> iterator() {
                    return new EntrySetIterator();
                }

                @Override
                public int size() {
                    return size;
                }
            };
        }
        return entrySet;
    }

    private class EntrySetIterator implements Iterator<Entry<K, V>> {
        private int ind = 0;
        private Iterator<AbstractMap.SimpleEntry<K, V>> element;

        public EntrySetIterator() {
            element = data.get(0).iterator();
            if (!element.hasNext()) {
                nextInd();
            }
        }

        private void nextInd() {
            ind++;
            while (ind < data.size() && data.get(ind).isEmpty()) {
                ind++;
            }
            if (ind != data.size()) {
                element = data.get(ind).iterator();
            }
        }

        @Override
        public boolean hasNext() {
            if (ind == data.size()) {
                return false;
            }
            if (element.hasNext()) {
                return true;
            }
            nextInd();
            return element.hasNext();
        }

        @Override
        public Entry<K, V> next() {
            if (ind == data.size())
                throw new NoSuchElementException();
            if (!element.hasNext()) {
                nextInd();
            }
            if (!element.hasNext()) {
                throw new NoSuchElementException();
            }
            return element.next();
        }

        @Override
        public void remove() {
            element.remove();
            size--;
        }
    }
}
