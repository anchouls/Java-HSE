package ru.hse.java.test;

import ru.hse.java.trie.MyTrie;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

public class TrieTest {

    @Test
    public void testAdd() {
        MyTrie trie = new MyTrie();
        Assertions.assertTrue(trie.add("abacaba"));
        Assertions.assertTrue(trie.contains("abacaba"));
    }

    @Test
    public void testAddInvalidString() {
        MyTrie trie = new MyTrie();
        Assertions.assertThrows(IllegalArgumentException.class, () -> trie.add("abac1aba"));
    }

    @Test
    public void testAddCorrectString() {
        MyTrie trie = new MyTrie();
        Assertions.assertTrue(trie.add("AbAcAbA"));
    }

    @Test
    public void testAddEmptyString() {
        MyTrie trie = new MyTrie();
        Assertions.assertTrue(trie.add(""));
        Assertions.assertEquals(1, trie.size());
    }

    @Test
    public void testAddTwoSameStrings() {
        MyTrie trie = new MyTrie();
        trie.add("abacaba");
        Assertions.assertFalse(trie.add("abacaba"));
    }

    @Test
    public void testAddManyStrings() {
        MyTrie trie = new MyTrie();
        Random random = new Random();
        Set<String> strings = new HashSet<>();
        for (int i = 0; i < 10000; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int j = 0; j < 100; j++) {
                int c = 97 + Math.abs(random.nextInt()) % 26;
                stringBuilder.append((char) c);
            }
            String newString = stringBuilder.toString();
            if (strings.contains(newString))
                Assertions.assertFalse(trie.add(newString));
            else
                Assertions.assertTrue(trie.add(newString));
            strings.add(newString);
        }
    }

    @Test
    public void testContains() {
        MyTrie trie = new MyTrie();
        trie.add("testContains");
        Assertions.assertTrue(trie.contains("testContains"));
    }

    @Test
    public void testNotContains() {
        MyTrie trie = new MyTrie();
        Assertions.assertFalse(trie.contains("testNotContains"));
    }

    @Test
    public void testNotContainsEmptyString() {
        MyTrie trie = new MyTrie();
        Assertions.assertFalse(trie.contains(""));
    }

    @Test
    public void testContainsEmptyString() {
        MyTrie trie = new MyTrie();
        trie.add("");
        Assertions.assertTrue(trie.contains(""));
    }

    @Test
    public void testNotContainsSubstring() {
        MyTrie trie = new MyTrie();
        trie.add("abacaba");
        Assertions.assertFalse(trie.contains("abacab"));
    }

    @Test
    public void testNotContainsOverstring() {
        MyTrie trie = new MyTrie();
        trie.add("abacab");
        Assertions.assertFalse(trie.contains("abacaba"));
    }

    @Test
    public void testNotContainsDeletedString() {
        MyTrie trie = new MyTrie();
        trie.add("abacaba");
        trie.remove("abacaba");
        Assertions.assertFalse(trie.contains("abacaba"));
    }

    @Test
    public void testContainsManyString() {
        MyTrie trie = new MyTrie();
        Random random = new Random();
        Set<String> strings = new HashSet<>();
        for (int i = 0; i < 10000; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int j = 0; j < 100; j++) {
                int c = 97 + Math.abs(random.nextInt()) % 26;
                stringBuilder.append((char) c);
            }
            String newString = stringBuilder.toString();
            if (strings.contains(newString))
                Assertions.assertTrue(trie.contains(newString));
            else
                Assertions.assertFalse(trie.contains(newString));
            strings.add(newString);
        }
    }

    @Test
    public void testRemoveNotExistingString() {
        MyTrie trie = new MyTrie();
        Assertions.assertFalse(trie.remove("string"));
    }

    @Test
    public void testRemoveExistingString() {
        MyTrie trie = new MyTrie();
        trie.add("string");
        Assertions.assertTrue(trie.remove("string"));
    }

    @Test
    public void testNotRemoveEmptyString() {
        MyTrie trie = new MyTrie();
        Assertions.assertFalse(trie.remove(""));
    }

    @Test
    public void testRemoveEmptyString() {
        MyTrie trie = new MyTrie();
        trie.add("");
        Assertions.assertTrue(trie.remove(""));
    }

    @Test
    public void testRemoveAfterAddingManyTimes() {
        MyTrie trie = new MyTrie();
        trie.add("AbAcAbA");
        trie.add("AbAcAbA");
        trie.add("AbAcAbA");
        Assertions.assertTrue(trie.contains("AbAcAbA"));
        trie.remove("AbAcAbA");
        Assertions.assertFalse(trie.contains("AbAcAbA"));
    }

    @Test
    public void testRemoveReducesNumberOfStrings() {
        MyTrie trie = new MyTrie();
        trie.add("AbAcAbA");
        int size = trie.size();
        trie.remove("AbAcAbA");
        Assertions.assertEquals(size - 1, trie.size());
    }

    @Test
    public void testRemoveManyString() {
        MyTrie trie = new MyTrie();
        Random random = new Random();
        Set<String> strings = new HashSet<>();
        for (int i = 0; i < 10000; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int j = 0; j < 100; j++) {
                int c = 97 + Math.abs(random.nextInt()) % 26;
                stringBuilder.append((char) c);
            }
            String newString = stringBuilder.toString();
            strings.add(newString);
            trie.add(newString);
        }
        int size = trie.size();
        for (String string : strings) {
            Assertions.assertTrue(trie.remove(string));
            size--;
            Assertions.assertEquals(size, trie.size());
        }
        Assertions.assertEquals(0, trie.size());
    }

    @Test
    public void testEmptyTrieSize() {
        MyTrie trie = new MyTrie();
        Assertions.assertEquals(0, trie.size());
    }

    @Test
    public void testSizeOfTrieWithEmptyString() {
        MyTrie trie = new MyTrie();
        trie.add("");
        Assertions.assertEquals(1, trie.size());
    }

    @Test
    public void testSize() {
        MyTrie trie = new MyTrie();
        trie.add("abacaba");
        trie.add("hi");
        trie.add("hello");
        Assertions.assertEquals(3, trie.size());
    }

    @Test
    public void testRemoveNotExistingStringNotChangeSize() {
        MyTrie trie = new MyTrie();
        trie.add("abacaba");
        trie.add("hi");
        trie.add("hello");
        trie.remove("hell");
        Assertions.assertEquals(3, trie.size());
    }

    @Test
    public void testAddSameStringsNotChangeSize() {
        MyTrie trie = new MyTrie();
        trie.add("abacaba");
        trie.add("abacaba");
        trie.add("abacaba");
        trie.add("abacaba");
        Assertions.assertEquals(1, trie.size());
    }

    @Test
    public void testSizeBigTrie() {
        MyTrie trie = new MyTrie();
        Random random = new Random();
        Set<String> strings = new HashSet<>();
        for (int i = 0; i < 10000; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int j = 0; j < 100; j++) {
                int c = 97 + Math.abs(random.nextInt()) % 26;
                stringBuilder.append((char) c);
            }
            String newString = stringBuilder.toString();
            trie.add(newString);
            strings.add(newString);
        }
        Assertions.assertEquals(strings.size(), trie.size());
    }

    @Test
    public void testHowManyStartsWithPrefix() {
        MyTrie trie = new MyTrie();
        trie.add("melancholy");
        trie.add("melancholic");
        trie.add("mel");
        trie.add("kind");
        Assertions.assertEquals(3, trie.howManyStartsWithPrefix("mel"));
    }

    @Test
    public void testHowManyStartsWithPrefixAfterRemove() {
        MyTrie trie = new MyTrie();
        trie.add("melancholy");
        trie.add("melancholic");
        trie.add("mel");
        trie.add("kind");
        trie.remove("mel");
        Assertions.assertEquals(2, trie.howManyStartsWithPrefix("mel"));
    }

    @Test
    public void testHowManyStartsWithNotExistingPrefix1() {
        MyTrie trie = new MyTrie();
        trie.add("melancholy");
        trie.add("melancholic");
        trie.add("mel");
        trie.add("kind");
        Assertions.assertEquals(0, trie.howManyStartsWithPrefix("hello"));
    }

    @Test
    public void testHowManyStartsWithNotExistingPrefix2() {
        MyTrie trie = new MyTrie();
        trie.add("melancholy");
        trie.add("melancholic");
        trie.add("mel");
        Assertions.assertEquals(0, trie.howManyStartsWithPrefix("melancholia"));
    }

    @Test
    public void testHowManyStartsWithNotExistingPrefix3() {
        MyTrie trie = new MyTrie();
        trie.add("melancholy");
        trie.add("melancholic");
        trie.add("mel");
        Assertions.assertEquals(0, trie.howManyStartsWithPrefix("melt"));
    }

    @Test
    public void testHowManyStartsWithEmptyString() {
        MyTrie trie = new MyTrie();
        trie.add("");
        trie.add("melancholy");
        trie.add("melancholic");
        trie.add("mel");
        Assertions.assertEquals(4, trie.howManyStartsWithPrefix(""));
    }

    @Test
    public void testHowManyStartsWithPrefixBamboo() {
        MyTrie trie = new MyTrie();
        StringBuilder stringBuilder = new StringBuilder();
        for (char c = 'a'; c < 't'; c++) {
            String prefix = stringBuilder.toString();
            stringBuilder.append(c);
            stringBuilder.append(prefix);
            trie.add(stringBuilder.toString());
        }
        StringBuilder stringBuilder1 = new StringBuilder();
        for (char c = 'a'; c < 'g'; c++) {
            String prefix = stringBuilder1.toString();
            stringBuilder1.append(c);
            stringBuilder1.append(prefix);
            String newString = stringBuilder1.toString();
            Assertions.assertEquals(19 - (c - 'a'), trie.howManyStartsWithPrefix(newString));
        }
    }

    @Test
    public void testNextStringWithNegativeK() {
        MyTrie trie = new MyTrie();
        Assertions.assertThrows(IllegalArgumentException.class, () -> trie.nextString("she", -1));
    }

    @Test
    public void testNextStringWithKEqualZero() {
        MyTrie trie = new MyTrie();
        trie.add("she");
        trie.add("he");
        trie.add("hers");
        trie.add("his");
        Assertions.assertEquals("he", trie.nextString("he", 0));
        Assertions.assertEquals("she", trie.nextString("she", 0));
        Assertions.assertEquals("hers", trie.nextString("hers", 0));
        Assertions.assertEquals("his", trie.nextString("his", 0));
    }

    @Test
    public void testNextString() {
        MyTrie trie = new MyTrie();
        trie.add("she");
        trie.add("he");
        trie.add("hers");
        trie.add("his");
        Assertions.assertEquals("his", trie.nextString("he", 2));
        Assertions.assertEquals("she", trie.nextString("he", 3));
        Assertions.assertEquals("hers", trie.nextString("he", 1));
        Assertions.assertEquals("she", trie.nextString("his", 1));
    }

    @Test
    public void testNextStringWithBigK() {
        MyTrie trie = new MyTrie();
        trie.add("she");
        trie.add("he");
        trie.add("hers");
        trie.add("his");
        Assertions.assertNull(trie.nextString("she", 2));
        Assertions.assertNull(trie.nextString("he", 4));
    }

    @Test
    public void testNextStringWithNotExistingString() {
        MyTrie trie = new MyTrie();
        trie.add("she");
        trie.add("he");
        trie.add("hers");
        trie.add("his");
        Assertions.assertEquals("his", trie.nextString("hffff", 1));
        Assertions.assertEquals("she", trie.nextString("hffff", 2));
    }

    @Test
    public void testNextStringWithNotExistingStringAndKEqualZero() {
        MyTrie trie = new MyTrie();
        trie.add("she");
        trie.add("he");
        trie.add("hers");
        trie.add("his");
        Assertions.assertNull(trie.nextString("hffff", 0));
    }

    @Test
    public void testNextStringWithNotExistingStringAndBigK() {
        MyTrie trie = new MyTrie();
        trie.add("she");
        trie.add("he");
        trie.add("hers");
        trie.add("his");
        Assertions.assertNull(trie.nextString("hffff", 3));
    }

    @Test
    public void testNextStringBamboo() {
        MyTrie trie = new MyTrie();
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<String> strings = new ArrayList<>();
        for (char c = 'a'; c < 'k'; c++) {
            String prefix = stringBuilder.toString();
            stringBuilder.append(c);
            stringBuilder.append(prefix);
            String newString = stringBuilder.toString();
            trie.add(newString);
            strings.add(newString);
        }
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 10 - i; j++) {
                Assertions.assertEquals(strings.get(i + j), trie.nextString(strings.get(i), j));
            }
        }
    }

    @Test
    public void testNextStringFromEmptyString() {
        MyTrie trie = new MyTrie();
        trie.add("a");
        trie.add("b");
        trie.add("c");
        trie.add("d");
        Assertions.assertNull(trie.nextString("", 0));
        Assertions.assertEquals("a", trie.nextString("", 1));
        Assertions.assertEquals("b", trie.nextString("", 2));
        Assertions.assertEquals("c", trie.nextString("", 3));
    }

    @Test
    public void testNextStringAfterEmptyString() {
        MyTrie trie = new MyTrie();
        trie.add("");
        trie.add("a");
        trie.add("b");
        Assertions.assertEquals("", trie.nextString("", 0));
        Assertions.assertEquals("a", trie.nextString("", 1));
        Assertions.assertEquals("b", trie.nextString("", 2));
    }

    @Test
    public void testAddInvalidStringSizeDoesNotChange() {
        MyTrie trie = new MyTrie();
        trie.add("he");
        trie.add("here");
        int oldSize = trie.size();
        Assertions.assertThrows(IllegalArgumentException.class, () -> trie.add("he1"));
        Assertions.assertEquals(oldSize, trie.size());
    }

    @Test
    public void testAddContainsRemove() {
        MyTrie trie = new MyTrie();
        Random random = new Random();
        Set<String> strings = new HashSet<>();
        for (int i = 0; i < 10000000; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int j = 0; j < 100; j++) {
                int c = 97 + Math.abs(random.nextInt()) % 26;
                stringBuilder.append((char) c);
            }
            int isWrong = Math.abs(random.nextInt()) % 2;
            if (isWrong == 1) {
                stringBuilder.append((char) (Math.abs(random.nextInt()) % 65));
            }
            String newString = stringBuilder.toString();
            int operation = Math.abs(random.nextInt()) % 3;
            switch (operation) {
                case 0:
                    if (isWrong == 0) {
                        strings.add(newString);
                        trie.add(newString);
                    } else {
                        Assertions.assertThrows(IllegalArgumentException.class, () -> trie.add(newString));
                    }
                    Assertions.assertEquals(strings.size(), trie.size());
                    break;
                case 1:
                    Assertions.assertEquals(strings.contains(newString), trie.contains(newString));
                    break;
                case 2:
                    if (!strings.isEmpty()) {
                        String s = strings.iterator().next();
                        Assertions.assertTrue(trie.remove(s));
                        strings.remove(s);
                    }
                    break;
            }
        }
    }
}