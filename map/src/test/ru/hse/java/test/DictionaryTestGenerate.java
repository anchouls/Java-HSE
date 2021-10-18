package ru.hse.java.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.hse.java.util.DictionaryImpl;

import java.util.*;

public class DictionaryTestGenerate {
    DictionaryImpl<Integer, Integer> map;

    @BeforeEach
    void generateSimple() {
        map = new DictionaryImpl<>();
        map.put(1, 2);
        map.put(2, 3);
        map.put(3, 4);
    }

    @Test
    void testSizeAfterPut() {
        Assertions.assertEquals(3, map.size());
        map.put(3, 5);
    }

    @Test
    void testSizeAfterReplace() {
        map.put(3, 5);
        Assertions.assertEquals(3, map.size());
    }

    @Test
    void testSizeAfterRemove() {
        map.remove(2);
        Assertions.assertEquals(2, map.size());
    }

    @Test
    void testSizeAfterClear() {
        map.clear();
        Assertions.assertEquals(0, map.size());
    }

    @Test
    void testContainsKeyAfterPutTrue() {
        Assertions.assertTrue(map.containsKey(1));
    }

    @Test
    void testContainsKeyAfterPutFalse() {
        Assertions.assertFalse(map.containsKey(10));
    }

    @Test
    void testContainsKeyAfterRemove() {
        map.remove(1);
        Assertions.assertFalse(map.containsKey(1));
    }

    @Test
    void testContainsValueAfterPutTrue() {
        Assertions.assertTrue(map.containsValue(2));
    }

    @Test
    void testContainsValueAfterPutFalse() {
        Assertions.assertFalse(map.containsValue(10));
    }

    @Test
    void testContainsValueAfterRemove() {
        map.remove(1);
        Assertions.assertFalse(map.containsValue(2));
    }

    @Test
    void testContainsOtherValueAfterRemove() {
        map.put(2, 2);
        map.remove(1);
        Assertions.assertTrue(map.containsValue(2));
    }

    @Test
    void testContainsValueAfterClear() {
        map.clear();
        Assertions.assertFalse(map.containsValue(2));
    }

    @Test
    void testGetAfterPut() {
        Assertions.assertEquals(2, map.get(1));
    }

    @Test
    void testGetNotExist() {
        Assertions.assertNull(map.get(10));
    }

    @Test
    void testGetAfterRemove() {
        map.remove(1);
        Assertions.assertNull(map.get(1));
    }

    @Test
    void testGetAfterReplace() {
        map.put(1, 1);
        Assertions.assertEquals(1, map.get(1));
    }

    @Test
    void testGenNullKey() {
        Assertions.assertThrows(NullPointerException.class, () -> map.get(null));
    }

    @Test
    void testPutSize() {
        Assertions.assertEquals(3, map.size());
    }

    @Test
    void testPutReplace() {
        Assertions.assertEquals(2, map.put(1, 5));
    }

    @Test
    void testPut() {
        Assertions.assertEquals(3, map.get(2));
    }

    @Test
    void testRemoveKey() {
        map.remove(1);
        Assertions.assertFalse(map.containsKey(1));
    }

    @Test
    void testRemoveValue() {
        map.remove(1);
        Assertions.assertFalse(map.containsValue(2));
    }

    @Test
    void testRemoveAll() {
        map.remove(1);
        map.remove(2);
        map.remove(3);
        Assertions.assertTrue(map.isEmpty());
    }

    @Test
    void testRemoveNotExist() {
        Assertions.assertNull(map.remove(155));
    }

    @Test
    void testKeySetIteratorNextThrow() {
        Iterator<Integer> it = map.keySet().iterator();
        it.next();
        it.next();
        it.next();
        Assertions.assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void testKeySetIteratorNextThrowOptimization() {
        Iterator<Integer> it = map.keySet().iterator();
        it.next();
        it.next();
        it.next();
        Assertions.assertThrows(NoSuchElementException.class, it::next);
        Assertions.assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void testKeySetIteratorHasNextAfterNext() {
        Set<Integer> set = map.keySet();
        set.iterator().next();
        Assertions.assertTrue(set.iterator().hasNext());
    }

    @Test
    void testKeySetIteratorRemoveSimple() {
        Set<Integer> set = map.keySet();
        Iterator<Integer> it = set.iterator();
        while (it.hasNext()) {
            if (it.next() == 2) {
                it.remove();
            }
        }
        Assertions.assertEquals(new HashSet<>(Arrays.asList(1, 3)), set);
    }

    @Test
    void testKeySetRemoveCheckValues() {
        Iterator<Integer> it = map.keySet().iterator();
        for (int i = 0; i < 3; i++) {
            it.next();
            it.remove();
        }
        Assertions.assertNull(map.get(1));
        Assertions.assertNull(map.get(2));
        Assertions.assertNull(map.get(3));
    }

    @Test
    void testEntrySetIteratorHasNextAfterNext() {
        Set<Integer> set = map.keySet();
        set.iterator().next();
        Assertions.assertTrue(set.iterator().hasNext());
    }

    @Test
    void testEntrySetIteratorNextThrow() {
        Iterator<Map.Entry<Integer, Integer>> it = map.entrySet().iterator();
        it.next();
        it.next();
        it.next();
        Assertions.assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void testEntrySetIteratorNextThrowOptimization() {
        Iterator<Map.Entry<Integer, Integer>> it = map.entrySet().iterator();
        it.next();
        it.next();
        it.next();
        Assertions.assertThrows(NoSuchElementException.class, it::next);
        Assertions.assertThrows(NoSuchElementException.class, it::next);
    }


    @Test
    void testEntrySetIteratorRemoveSimple() {
        Set<Map.Entry<Integer, Integer>> set = map.entrySet();
        Iterator<Map.Entry<Integer, Integer>> it = set.iterator();
        while (it.hasNext()) {
            if (it.next().getValue() == 3) {
                it.remove();
            }
        }
        Assertions.assertEquals(new HashSet<>(Arrays.asList(1, 3)), map.keySet());
    }

    @Test
    void testEntrySetRemoveCheckValues() {
        Iterator<Map.Entry<Integer, Integer>> it = map.entrySet().iterator();
        for (int i = 0; i < 3; i++) {
            it.next();
            it.remove();
        }
        Assertions.assertNull(map.get(1));
        Assertions.assertNull(map.get(2));
        Assertions.assertNull(map.get(3));
    }

    @Test
    void testValuesIteratorHasNextAfterNext() {
        Collection<Integer> coll = map.values();
        coll.iterator().next();
        Assertions.assertTrue(coll.iterator().hasNext());
    }

    @Test
    void testValuesIteratorNextThrow() {
        Iterator<Integer> it = map.values().iterator();
        it.next();
        it.next();
        it.next();
        Assertions.assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void testValuesIteratorNextThrowOptimization() {
        Iterator<Integer> it = map.values().iterator();
        it.next();
        it.next();
        it.next();
        Assertions.assertThrows(NoSuchElementException.class, it::next);
        Assertions.assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void testValuesIteratorRemoveSimple() {
        Collection<Integer> coll = map.values();
        Iterator<Integer> it = coll.iterator();
        while (it.hasNext()) {
            if (it.next() == 3) {
                it.remove();
            }
        }
        Assertions.assertEquals(new HashSet<>(Arrays.asList(2, 4)), new HashSet<>(coll));
    }

    @Test
    void testValuesRemoveCheckValues() {
        Iterator<Integer> it = map.values().iterator();
        for (int i = 0; i < 3; i++) {
            it.next();
            it.remove();
        }
        Assertions.assertNull(map.get(1));
        Assertions.assertNull(map.get(2));
        Assertions.assertNull(map.get(3));
    }

    @Test
    void testRemoveExc() {
        Iterator<Integer> iterator = map.keySet().iterator();
        iterator.next();
        iterator.remove();
        Assertions.assertThrows(IllegalStateException.class, iterator::remove);
    }

}


