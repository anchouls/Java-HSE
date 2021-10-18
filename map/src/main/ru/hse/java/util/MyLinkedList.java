package ru.hse.java.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MyLinkedList<E> extends AbstractCollection<E> {
    private final Node head;
    private Node tail;
    private int size;

    public MyLinkedList() {
        head = new Node(null, null);
        tail = head;
    }

    private class Node {
        private final E value;
        private Node next = null;
        private Node prev;

        public Node(E value, Node prev) {
            this.value = value;
            this.prev = prev;
        }

    }

    @Override
    public @NotNull Iterator<E> iterator() {
        return new Iterator<>() {
            Node cur = head;

            @Override
            public boolean hasNext() {
                return cur.next != null;
            }

            @Override
            public E next() {
                E value = cur.next.value;
                cur = cur.next;
                return value;
            }

            @Override
            public void remove() {
                if (cur == head) {
                    throw new NoSuchElementException();
                }
                if (tail == cur) {
                    tail = cur.prev;
                }
                if (cur.prev == null) {
                    throw new IllegalStateException();
                }
                cur.prev.next = cur.next;
                if (cur.next != null) {
                    cur.next.prev = cur.prev;
                }
                cur.prev = null;
                size--;
            }
        };
    }

    @Override
    public boolean add(E e) {
        tail.next = new Node(e, tail);
        tail = tail.next;
        size++;
        return true;
    }

    @Override
    public int size() {
        return size;
    }
}
