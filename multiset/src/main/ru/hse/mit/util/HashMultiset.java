package ru.hse.mit.util;

import java.util.*;

import org.jetbrains.annotations.NotNull;

public class HashMultiset<E> extends AbstractCollection<E> implements Multiset<E> {
    private final Map<E, Integer> data = new LinkedHashMap<>();
    private int size = 0;

    @Override
    public int count(Object element) {
        return data.getOrDefault(element, 0);
    }

    @Override
    public Set<E> elementSet() {
        return data.keySet();
    }

    @Override
    public Set<Entry<E>> entrySet() {
        return new AbstractSet<>() {
            @Override
            public @NotNull Iterator<Entry<E>> iterator() {
                return new Iterator<>() {
                    private final Iterator<Map.Entry<E, Integer>> iterator = data.entrySet().iterator();
                    private int entrySize;

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public Entry<E> next() {
                        Map.Entry<E, Integer> next = iterator.next();
                        entrySize = next.getValue();
                        return new Entry<>() {
                            @Override
                            public E getElement() {
                                return next.getKey();
                            }

                            @Override
                            public int getCount() {
                                return next.getValue();
                            }
                        };
                    }

                    @Override
                    public void remove() {
                        iterator.remove();
                        size -= entrySize;
                    }
                };
            }

            @Override
            public int size() {
                return data.size();
            }
        };
    }

    @Override
    public @NotNull Iterator<E> iterator() {
        return new Iterator<>() {
            private final Iterator<Map.Entry<E, Integer>> iterator = data.entrySet().iterator();
            private E element;
            private int elementNumber;
            private boolean deleted = true;

            @Override
            public boolean hasNext() {
                return elementNumber > 0 || iterator.hasNext();
            }

            @Override
            public E next() {
                deleted = false;
                if (elementNumber == 0) {
                    Map.Entry<E, Integer> next = iterator.next();
                    element = next.getKey();
                    elementNumber = next.getValue();
                }
                elementNumber--;
                return element;
            }

            @Override
            public void remove() {
                if (deleted) {
                    throw new IllegalStateException();
                }
                int number = data.get(element);
                if (number == 1) {
                    iterator.remove();
                } else {
                    data.put(element, number - 1);
                }
                size--;
                deleted = true;
            }
        };
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean contains(Object element) {
        return data.containsKey(element);
    }


    @Override
    public boolean add(E element) {
        data.put(element, data.getOrDefault(element, 0) + 1);
        size++;
        return true;
    }

    @Override
    public boolean remove(Object element) {
        if (!data.containsKey(element)) {
            return false;
        }
        int number = data.get(element);
        if (number == 1) {
            data.remove(element);
        } else {
            data.put((E) element, number - 1);
        }
        size--;
        return true;
    }
}
