package ru.hse.java.test.collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.hse.java.collections.Collections;
import ru.hse.java.functional.*;

import java.util.ArrayList;
import java.util.List;

public class CollectionsTests {
    @Test
    void testMap() {
        List<Integer> list = new ArrayList<>();
        List<Integer> expected = new ArrayList<>();
        list.add(0);
        expected.add(0);
        list.add(1);
        expected.add(1);
        list.add(2);
        expected.add(4);
        Function1<Integer, Integer> f = x -> x * x;
        List<Integer> result = Collections.map(f, list);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testMapEmptyList() {
        List<Integer> list = new ArrayList<>();
        List<Integer> expected = new ArrayList<>();
        Function1<Integer, Integer> f = x -> x * x;
        List<Integer> result = Collections.map(f, list);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testMapIdentity() {
        List<Integer> list = new ArrayList<>();
        Function1<Integer, Integer> f = x -> x;
        List<Integer> result = Collections.map(f, list);
        Assertions.assertEquals(list, result);
    }

    @Test
    void testMapStress() {
        List<Integer> list = new ArrayList<>();
        List<Integer> expected = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(i);
            expected.add(i * i);
        }
        Function1<Integer, Integer> f = x -> x * x;
        List<Integer> result = Collections.map(f, list);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testFilter() {
        List<Integer> list = new ArrayList<>();
        List<Integer> expected = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        expected.add(2);
        expected.add(4);
        expected.add(6);
        Predicate<Integer> p = x -> x % 2 == 0;
        List<Integer> result = Collections.filter(p, list);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testFilterStress() {
        List<Integer> list = new ArrayList<>();
        List<Integer> expected = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(i);
            if (i % 2 == 0) {
                expected.add(i);
            }
        }
        Predicate<Integer> p = x -> x % 2 == 0;
        List<Integer> result = Collections.filter(p, list);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testFilterWithAlwaysTrue() {
        List<Integer> list = new ArrayList<>();
        List<Integer> expected = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(i);
            expected.add(i);
        }
        List<Integer> result = Collections.filter(Predicate.ALWAYS_TRUE(), list);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testFilterWithAlwaysFalse() {
        List<Integer> list = new ArrayList<>();
        List<Integer> expected = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(i);
        }
        List<Integer> result = Collections.filter(Predicate.ALWAYS_FALSE(), list);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testFilterEmptyList() {
        List<Integer> list = new ArrayList<>();
        Predicate<Integer> p = x -> x % 2 == 0;
        List<Integer> result = Collections.filter(p, list);
        Assertions.assertEquals(list, result);
    }

    @Test
    void testTakeWhile() {
        List<Integer> list = new ArrayList<>();
        List<Integer> expected = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        expected.add(1);
        expected.add(2);
        expected.add(3);
        Predicate<Integer> p = x -> x < 4;
        List<Integer> result = Collections.takeWhile(p, list);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testTakeWhileStress() {
        List<Integer> list = new ArrayList<>();
        List<Integer> expected = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(i);
            if (i < 500) {
                expected.add(i);
            }
        }
        Predicate<Integer> p = x -> x < 500;
        List<Integer> result = Collections.takeWhile(p, list);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testTakeWhileWithAlwaysTrue() {
        List<Integer> list = new ArrayList<>();
        List<Integer> expected = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(i);
            expected.add(i);
        }
        List<Integer> result = Collections.takeWhile(Predicate.ALWAYS_TRUE(), list);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testTakeWhileWithAlwaysFalse() {
        List<Integer> list = new ArrayList<>();
        List<Integer> expected = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(i);
        }
        List<Integer> result = Collections.takeWhile(Predicate.ALWAYS_FALSE(), list);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testTakeWhileEmptyList() {
        List<Integer> list = new ArrayList<>();
        Predicate<Integer> p = x -> x < 4;
        List<Integer> result = Collections.takeWhile(p, list);
        Assertions.assertEquals(list, result);
    }

    @Test
    void testTakeUnless() {
        List<Integer> list = new ArrayList<>();
        List<Integer> expected = new ArrayList<>();
        list.add(6);
        list.add(5);
        list.add(4);
        list.add(3);
        list.add(2);
        list.add(1);
        expected.add(6);
        expected.add(5);
        expected.add(4);
        Predicate<Integer> p = x -> x < 4;
        List<Integer> result = Collections.takeUnless(p, list);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testTakeUnlessStress() {
        List<Integer> list = new ArrayList<>();
        List<Integer> expected = new ArrayList<>();
        for (int i = 1000; i >= 0; i--) {
            list.add(i);
            if (i >= 500) {
                expected.add(i);
            }
        }
        Predicate<Integer> p = x -> x < 500;
        List<Integer> result = Collections.takeUnless(p, list);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testTakeUnlessWithAlwaysTrue() {
        List<Integer> list = new ArrayList<>();
        List<Integer> expected = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(i);
        }
        List<Integer> result = Collections.takeUnless(Predicate.ALWAYS_TRUE(), list);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testTakeUnlessWithAlwaysFalse() {
        List<Integer> list = new ArrayList<>();
        List<Integer> expected = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(i);
            expected.add(i);
        }
        List<Integer> result = Collections.takeUnless(Predicate.ALWAYS_FALSE(), list);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testTakeUnlessEmptyList() {
        List<Integer> list = new ArrayList<>();
        Predicate<Integer> p = x -> x < 4;
        List<Integer> result = Collections.takeUnless(p, list);
        Assertions.assertEquals(list, result);
    }

    @Test
    void testFoldlMultiplication() {
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            list.add(i);
        }
        Function2<Integer, Integer, Integer> f = (x, y) -> x * y;
        Assertions.assertEquals(362880, Collections.foldl(f, 1, list));
    }

    @Test
    void testFoldlSum() {
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i < 10000; i++) {
            list.add(i);
        }
        Function2<Integer, Integer, Integer> f = Integer::sum;
        Assertions.assertEquals(49995001, Collections.foldl(f, 1, list));
    }

    @Test
    void testFoldlEmptyList() {
        List<Integer> list = new ArrayList<>();
        Function2<Integer, Integer, Integer> f = (x, y) -> x * y;
        Assertions.assertEquals(1, Collections.foldl(f, 1, list));
    }

    @Test
    void testFoldrMultiplication() {
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            list.add(i);
        }
        Function2<Integer, Integer, Integer> f = (x, y) -> x * y;
        Assertions.assertEquals(362880, Collections.foldr(f, 1, list));
    }

    @Test
    void testFoldrSum() {
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i < 1000; i++) {
            list.add(i);
        }
        Function2<Integer, Integer, Integer> f = Integer::sum;
        Assertions.assertEquals(499501, Collections.foldr(f, 1, list));
    }

    @Test
    void testFoldrDivision() {
        Function2<Integer, Integer, Integer> f = (x, y) -> x / y;
        Assertions.assertEquals(4, Collections.foldr(f, 2, java.util.Arrays.asList(16, 8, 4)));
    }

    @Test
    void testFoldlDivision() {
        Function2<Integer, Integer, Integer> f = (x, y) -> x / y;
        Assertions.assertEquals(2, Collections.foldl(f, 16, java.util.Arrays.asList(4, 1, 2)));
    }

    @Test
    void testFoldrEmptyList() {
        List<Integer> list = new ArrayList<>();
        Function2<Integer, Integer, Integer> f = (x, y) -> x * y;
        Assertions.assertEquals(1, Collections.foldr(f, 1, list));
    }

    @Test
    void testFoldrAndFoldl() {
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            list.add(i);
        }
        Function2<Integer, Integer, Integer> f = (x, y) -> x * y;
        Assertions.assertEquals(Collections.foldl(f, 1, list), Collections.foldr(f, 1, list));
    }

    @Test
    void testMapFilterTakeWhileFoldl() {
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i < 100; i++) {
            list.add(i);
        }
        Function1<Integer, Integer> f = x -> x * x;
        Predicate<Integer> p = x -> x % 2 == 0;
        Predicate<Integer> p1 = x -> x < 500;
        Function2<Integer, Integer, Integer> f1 = Integer::sum;
        List<Integer> list1 = Collections.map(f, list);
        List<Integer> list2 = Collections.filter(p, list1);
        List<Integer> list3 = Collections.takeWhile(p1, list2);
        Assertions.assertEquals(2025, Collections.foldl(f1, 1, list3));
        List<Integer> list4 = Collections.takeUnless(p1.not(), list2);
        Assertions.assertEquals(2025, Collections.foldr(f1, 1, list4));
    }

    @Test
    void testTailRec() {
        List<Integer> list = java.util.Collections.nCopies(10_000, 1);
        Integer actual = Collections.foldr(Integer::sum, 3, list);

        Assertions.assertEquals(list.stream().reduce(3, Integer::sum), actual);
    }

}
