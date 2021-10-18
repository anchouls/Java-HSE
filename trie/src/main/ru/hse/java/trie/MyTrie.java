package ru.hse.java.trie;

import java.util.Map;
import java.util.TreeMap;

public class MyTrie implements Trie {

    private static class TrieNode {
        private final Map<Character, TrieNode> next = new TreeMap<>();
        private boolean isTerminal;
        private int sizeSubtree;
    }

    private final TrieNode root = new TrieNode();

    @Override
    public boolean add(String element) throws IllegalArgumentException {
        if (!isLatinWord(element)) {
            throw new IllegalArgumentException("string contains not only Latin letters");
        }
        if (contains(element)) {
            return false;
        }
        TrieNode currentPosition = root;
        currentPosition.sizeSubtree++;
        for (char c : element.toCharArray()) {
            if (!currentPosition.next.containsKey(c)) {
                currentPosition.next.put(c, new TrieNode());
            }
            currentPosition = currentPosition.next.get(c);
            currentPosition.sizeSubtree++;
        }
        currentPosition.isTerminal = true;
        return true;
    }

    private boolean isLatinWord(String element) {
        for (char c : element.toCharArray()) {
            if (!((c <= 'z' && c >= 'a') || (c <= 'Z' && c >= 'A'))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean contains(String element) {
        TrieNode currentPosition = root;
        for (char c : element.toCharArray()) {
            if (!currentPosition.next.containsKey(c)) {
                return false;
            }
            currentPosition = currentPosition.next.get(c);
        }
        return currentPosition.isTerminal;
    }

    @Override
    public boolean remove(String element) {
        if (!contains(element)) {
            return false;
        }
        TrieNode currentPosition = root;
        currentPosition.sizeSubtree--;
        for (char c : element.toCharArray()) {
            TrieNode nextPosition = currentPosition.next.get(c);
            if (nextPosition.sizeSubtree == 1) {
                currentPosition.next.remove(c);
                return true;
            }
            currentPosition = nextPosition;
            currentPosition.sizeSubtree--;
        }
        currentPosition.isTerminal = false;
        return true;
    }

    @Override
    public int size() {
        return root.sizeSubtree;
    }

    @Override
    public int howManyStartsWithPrefix(String prefix) {
        TrieNode currentPosition = root;
        for (char c : prefix.toCharArray()) {
            if (!currentPosition.next.containsKey(c)) {
                return 0;
            }
            currentPosition = currentPosition.next.get(c);
        }
        return currentPosition.sizeSubtree;
    }

    @Override
    public String nextString(String element, int k) throws IllegalArgumentException {
        if (k < 0) {
            throw new IllegalArgumentException("k is negative");
        }
        if (k == 0) {
            return contains(element) ? element : null;
        }
        int count = 0;
        TrieNode currentPosition = root;
        if (currentPosition.isTerminal) {
            count++;
        }
        boolean flag = false;
        for (char c : element.toCharArray()) {
            for (Map.Entry<Character, TrieNode> i : currentPosition.next.entrySet()) {
                if (i.getKey() < c) {
                    count += i.getValue().sizeSubtree;
                } else if (i.getKey() > c) {
                    flag = true;
                    break;
                } else {
                    currentPosition = i.getValue();
                    if (currentPosition.isTerminal) {
                        count += 1;
                    }
                    break;
                }
            }
            if (flag) {
                break;
            }
        }
        StringBuilder stringK = new StringBuilder();
        if (find(k + count, root, stringK)) {
            return stringK.toString();
        }
        return null;
    }

    private boolean find(int k, TrieNode currentPosition, StringBuilder stringK) {
        if (currentPosition.isTerminal) {
            if (k == 1) {
                return true;
            } else {
                k--;
            }
        }
        for (Map.Entry<Character, TrieNode> i : currentPosition.next.entrySet()) {
            if (i.getValue().sizeSubtree < k) {
                k -= i.getValue().sizeSubtree;
            } else {
                stringK.append(i.getKey());
                if (find(k, i.getValue(), stringK)) {
                    return true;
                }
                stringK.deleteCharAt(stringK.length() - 1);
            }
        }
        return false;
    }
}