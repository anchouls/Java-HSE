package ru.hse.java.test;

import ru.hse.java.util.DictionaryImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;


public class DictionaryTests {

    @Test
    public void testStress() {
        DictionaryImpl<Integer, String> dictionary = new DictionaryImpl<>();
        HashMap<Integer, String> hashMap = new HashMap<>();
        Random random = new Random();
        random.setSeed(14);
        for (int i = 0; i < 100000; i++) {
            int operation = random.nextInt() % 3;
            if (operation == 0) {  // remove
                if (hashMap.size() == 0) {
                    continue;
                }
                int oldSize = dictionary.size();
                Integer key = hashMap.keySet().iterator().next();
                hashMap.remove(key);
                dictionary.remove(key);
                Assertions.assertFalse(dictionary.containsKey(key));
                Assertions.assertNull(dictionary.get(key));
                Assertions.assertEquals(oldSize - 1, dictionary.size());
            } else {  // add
                int stringLen = random.nextInt() % 1000;
                StringBuilder stringBuilder = new StringBuilder();
                for (int j = 0; j < stringLen; j++) {
                    stringBuilder.append(random.nextInt() % 256);
                }
                String value = stringBuilder.toString();
                int key = random.nextInt();
                Assertions.assertEquals(hashMap.containsKey(key), dictionary.containsKey(key));
                dictionary.put(key, value);
                hashMap.put(key, value);
                Assertions.assertTrue(dictionary.containsValue(value));
                Assertions.assertTrue(dictionary.containsKey(key));
                Assertions.assertEquals(value, dictionary.get(key));
            }
        }
        Assertions.assertEquals(hashMap.size(), dictionary.size());
    }

    @Test
    public void testEmptyInit() {
        DictionaryImpl<Integer, String> dictionary = new DictionaryImpl<>();
        Assertions.assertTrue(dictionary.isEmpty());
    }

    @Test
    void testSizeInit() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        Assertions.assertEquals(0, map.size());
    }

    @Test
    void testContainsKeySomeHash() {
        Map<String, Integer> map = new DictionaryImpl<>();
        map.put("Aa", 1);
        Assertions.assertFalse(map.containsKey("BB"));
    }

    @Test
    void testNegateKey() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        map.put(-1, 20);
        Assertions.assertEquals(map.get(-1), 20);
    }

    @Test
    void testMaxKey() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        map.put(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Assertions.assertEquals(Integer.MAX_VALUE, map.get(Integer.MAX_VALUE));
    }

    @Test
    void testMinKey() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        map.put(Integer.MIN_VALUE, Integer.MIN_VALUE);
        Assertions.assertEquals(Integer.MIN_VALUE, map.get(Integer.MIN_VALUE));
    }

    @Test
    void testPutKeyIsEmptyString() {
        Map<String, Integer> map = new DictionaryImpl<>();
        map.put("", 1);
        Assertions.assertEquals(1, map.get(""));
    }

    @Test
    void testPutNullKey() {
        Map<String, Integer> map = new DictionaryImpl<>();
        Assertions.assertThrows(NullPointerException.class, () -> map.put(null, 1));
    }

    @Test
    void testPutAllBasic() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        Map<Integer, Integer> donorMap = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            donorMap.put(i, i * 2 + 1);
        }
        map.putAll(donorMap);
        Assertions.assertEquals(100, map.size());
        for (int i = 0; i < 100; i++) {
            Assertions.assertEquals(i * 2 + 1, map.get(i));
        }
    }

    @Test
    void testPutAllOver() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        Map<Integer, Integer> donorMap = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            donorMap.put(i, i * 2 + 1);
            map.put(100 + i, (100 + i) * 2 + 1);
        }
        map.putAll(donorMap);
        Assertions.assertEquals(200, map.size());
        for (int i = 0; i < 200; i++) {
            Assertions.assertEquals(i * 2 + 1, map.get(i));
        }
    }

    @Test
    void testClearEmpty() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        for (int i = 0; i < 100; i++) {
            map.put(i, i * 2 + 1);
        }
        map.clear();
        Assertions.assertTrue(map.isEmpty());
    }

    @Test
    void testClearNotExists() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        for (int i = 0; i < 100; i++) {
            map.put(i, i * 2 + 1);
        }
        map.clear();
        Assertions.assertNull(map.get(55));
    }

    @Test
    void testKeySetCorrectness() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        for (int i = 0; i < 100; i++) {
            map.put(i, i * 2 + 1);
        }
        Set<Integer> set = map.keySet();
        for (int i : set) {
            Assertions.assertEquals(i * 2 + 1, map.get(i));
        }
        for (int i = 0; i < 100; i++) {
            Assertions.assertTrue(set.contains(i));
        }
    }

    @Test
    void testKeySetHashMap() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        Map<Integer, Integer> hashMap = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            map.put(i, i * 2 + 1);
            hashMap.put(i, i * 2 + 1);
        }
        Assertions.assertEquals(map.keySet(), hashMap.keySet());
    }

    @Test
    void testManyKeySet() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        for (int i = 0; i < 100; i++) {
            map.put(i, i * 2 + 1);
        }
        Set<Integer> kS1 = map.keySet();
        map.put(199, 3);
        Set<Integer> kS2 = map.keySet();
        Assertions.assertEquals(kS1, kS2);
        Iterator<Integer> it1 = kS1.iterator();
        for (int i = 0; i < 50; i++) {
            it1.next();
        }
        int i = 0;
        for (int x : kS2) {
            i++;
        }
        Assertions.assertEquals(101, i);
        for (i = 50; i < 101; i++) {
            Assertions.assertTrue(it1.hasNext());
            it1.next();
        }
    }

    @Test
    void testKeySetDependence() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        for (int i = 0; i < 100; i++) {
            map.put(i, i * 2 + 1);
        }
        Set<Integer> set = map.keySet();
        for (int i = 100; i < 200; i++) {
            map.put(i, i * 2 + 1);
        }
        Assertions.assertEquals(set, map.keySet());
        Assertions.assertEquals(200, set.size());
        for (int i : set) {
            Assertions.assertEquals(i * 2 + 1, map.get(i));
        }
        for (int i = 0; i < 200; i++) {
            Assertions.assertTrue(set.contains(i));
        }
    }

    @Test
    void testKeySetIteratorHasNextEmpty() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        Set<Integer> set = map.keySet();
        Assertions.assertFalse(set.iterator().hasNext());
    }

    @Test
    void testKeySetIteratorHasNextSimple() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        map.put(1, 2);
        Set<Integer> set = map.keySet();
        Assertions.assertTrue(set.iterator().hasNext());
    }

    @Test
    void testKeySetIteratorNextSimple() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        map.put(1, 2);
        Set<Integer> set = map.keySet();
        Assertions.assertEquals(1, set.iterator().next());
    }

    @Test
    void testKeySetIteratorHasNextEnd() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        map.put(1, 2);
        Set<Integer> set = map.keySet();
        Iterator<Integer> it = set.iterator();
        it.next();
        Assertions.assertFalse(it.hasNext());
    }

    @Test
    void testKeySetRemoveAll() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        for (int i = 0; i < 100; i++) {
            map.put(i, i * 2 + 1);
        }
        Iterator<Integer> it = map.keySet().iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        Assertions.assertEquals(new HashSet<>(), map.keySet());
    }

    @Test
    void testKeySetIteratorNextHashNextStress() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        for (int i = 0; i < 10000; i++) {
            map.put(i, i * 2 + 1);
        }
        Set<Integer> hashSet = new HashSet<>();
        Set<Integer> set = map.keySet();
        Iterator<Integer> it = set.iterator();
        while (it.hasNext()) {
            hashSet.add(it.next());
        }
        Assertions.assertEquals(set, hashSet);
    }

    @Test
    void testEntrySetCorrectness() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        for (int i = 0; i < 100; i++) {
            map.put(i, i * 2 + 1);
        }
        Set<Map.Entry<Integer, Integer>> set = map.entrySet();
        for (Map.Entry<Integer, Integer> i : set) {
            Assertions.assertEquals(i.getValue(), map.get(i.getKey()));
        }
    }

    @Test
    void testEntrySetHashMap() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        Map<Integer, Integer> hashMap = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            map.put(i, i * 2 + 1);
            hashMap.put(i, i * 2 + 1);
        }
        Assertions.assertEquals(map.entrySet(), hashMap.entrySet());
    }

    @Test
    void testEntrySetDependence() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        for (int i = 0; i < 100; i++) {
            map.put(i, i * 2 + 1);
        }
        Set<Map.Entry<Integer, Integer>> set = map.entrySet();
        for (int i = 100; i < 200; i++) {
            map.put(i, i * 2 + 1);
        }
        Set<Map.Entry<Integer, Integer>> newSet = map.entrySet();
        Assertions.assertEquals(set, newSet);
    }

    @Test
    void testEntrySetIteratorHasNextEmpty() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        Set<Map.Entry<Integer, Integer>> set = map.entrySet();
        Assertions.assertFalse(set.iterator().hasNext());
    }

    @Test
    void testEntrySetIteratorHasNextSimple() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        map.put(1, 2);
        Set<Map.Entry<Integer, Integer>> set = map.entrySet();
        Assertions.assertTrue(set.iterator().hasNext());
    }

    @Test
    void testEntrySetIteratorNextSimple() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        map.put(1, 2);
        Set<Map.Entry<Integer, Integer>> set = map.entrySet();
        Assertions.assertEquals(2, set.iterator().next().getValue());
    }

    @Test
    void testEntrySetIteratorHasNextEnd() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        map.put(1, 2);
        Set<Map.Entry<Integer, Integer>> set = map.entrySet();
        Iterator<Map.Entry<Integer, Integer>> it = set.iterator();
        it.next();
        Assertions.assertFalse(it.hasNext());
    }

    @Test
    void testEntrySetRemoveAll() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        for (int i = 0; i < 100; i++) {
            map.put(i, i * 2 + 1);
        }
        Iterator<Map.Entry<Integer, Integer>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        Assertions.assertEquals(new HashSet<>(), map.entrySet());
    }

    @Test
    void testEntrySetIteratorNextHashNextStress() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        for (int i = 0; i < 10000; i++) {
            map.put(i, i * 2 + 1);
        }
        Set<Map.Entry<Integer, Integer>> hashSet = new HashSet<>();
        Set<Map.Entry<Integer, Integer>> set = map.entrySet();
        Iterator<Map.Entry<Integer, Integer>> it = set.iterator();
        while (it.hasNext()) {
            hashSet.add(it.next());
        }
        Assertions.assertEquals(set, hashSet);
    }

    @Test
    void testValuesCorrectness() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        for (int i = 0; i < 100; i++) {
            map.put(i, i * 2 + 1);
        }
        Collection<Integer> coll = map.values();
        for (int i : coll) {
            Assertions.assertTrue(map.containsValue(i));
        }
        for (int i = 0; i < 100; i++) {
            Assertions.assertTrue(coll.contains(i * 2 + 1));
        }
    }

    @Test
    void testValuesHashMap() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        Map<Integer, Integer> hashMap = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            map.put(i, i * 2 + 1);
            hashMap.put(i, i * 2 + 1);
        }
        Assertions.assertEquals(new HashSet<>(map.values()), new HashSet<>(hashMap.values()));
    }

    @Test
    void testValuesDependence() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        for (int i = 0; i < 100; i++) {
            map.put(i, i * 2 + 1);
        }
        Collection<Integer> coll = map.values();
        for (int i = 100; i < 200; i++) {
            map.put(i, i * 2 + 1);
        }
        Assertions.assertEquals(coll, map.values());
        Assertions.assertEquals(200, coll.size());
        for (int i : coll) {
            Assertions.assertTrue(map.containsValue(i));
        }
        for (int i = 0; i < 200; i++) {
            Assertions.assertTrue(coll.contains(2 * i + 1));
        }
    }

    @Test
    void testValuesIteratorHasNextEmpty() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        Collection<Integer> coll = map.values();
        Assertions.assertFalse(coll.iterator().hasNext());
    }

    @Test
    void testValuesIteratorHasNextSimple() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        map.put(1, 2);
        Collection<Integer> coll = map.values();
        Assertions.assertTrue(coll.iterator().hasNext());
    }

    @Test
    void testValuesIteratorNextSimple() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        map.put(1, 2);
        Collection<Integer> coll = map.values();
        Assertions.assertEquals(2, coll.iterator().next());
    }

    @Test
    void testValuesIteratorHasNextEnd() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        map.put(1, 2);
        Collection<Integer> coll = map.keySet();
        Iterator<Integer> it = coll.iterator();
        it.next();
        Assertions.assertFalse(it.hasNext());
    }

    @Test
    void testValuesRemoveAll() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        for (int i = 0; i < 100; i++) {
            map.put(i, i * 2 + 1);
        }
        Iterator<Integer> it = map.values().iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        Assertions.assertTrue(map.values().isEmpty());
    }

    @Test
    void testValuesIteratorNextHashNextStress() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        for (int i = 0; i < 10000; i++) {
            map.put(i, i * 2 + 1);
        }
        Collection<Integer> arr = new ArrayList<>();
        Collection<Integer> coll = map.values();
        Iterator<Integer> it = coll.iterator();
        while (it.hasNext()) {
            arr.add(it.next());
        }
        Assertions.assertEquals(new HashSet<>(arr), new HashSet<>(coll));
    }

    @Test
    void testDecreaseCapacity() {
        Map<Integer, Integer> map = new DictionaryImpl<>();
        for (int i = 0; i < 100; i++) {
            map.put(i, i * 2 + 1);
        }
        for (int i = 0; i < 100; i++) {
            map.remove(i);
        }
        Assertions.assertEquals(map.size(), 0);
    }
}
