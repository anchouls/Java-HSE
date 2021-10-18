package ru.hse.java.test.functional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.hse.java.functional.Predicate;

public class PredicateTests {
    @Test
    void testAlwaysTrue() {
        for (int i = 0; i < 1000; i++) {
            Assertions.assertTrue(Predicate.ALWAYS_TRUE().apply(i));
        }
    }

    @Test
    void testAlwaysFalse() {
        for (int i = 0; i < 1000; i++) {
            Assertions.assertFalse(Predicate.ALWAYS_FALSE().apply(i));
        }
    }

    @Test
    void testOr() {
        Predicate<Integer> p1 = x -> x % 2 == 0;
        Predicate<Integer> p2 = x -> x % 3 == 0;
        Predicate<Integer> result = p1.or(p2);
        Assertions.assertTrue(result.apply(6));
        Assertions.assertTrue(result.apply(2));
        Assertions.assertTrue(result.apply(3));
        Assertions.assertFalse(result.apply(1));
    }

    @Test
    void testOrAlwaysTrue() {
        Predicate<Integer> p1 = x -> x % 2 == 0;
        Predicate<Integer> result = p1.or(Predicate.ALWAYS_TRUE());
        Assertions.assertTrue(result.apply(6));
        Assertions.assertTrue(result.apply(2));
        Assertions.assertTrue(result.apply(3));
        Assertions.assertTrue(result.apply(1));
    }

    @Test
    void testOrAlwaysFalse() {
        Predicate<Integer> p1 = x -> x % 2 == 0;
        Predicate<Integer> result = p1.or(Predicate.ALWAYS_FALSE());
        Assertions.assertTrue(result.apply(6));
        Assertions.assertTrue(result.apply(2));
        Assertions.assertFalse(result.apply(3));
    }

    @Test
    void testOrStress() {
        Predicate<Integer> p = x -> x % 2 == 0;
        Predicate<Integer> result = p.or(p);
        for (int i = 0; i < 1000; i++) {
            result = p.or(result);
        }
        Assertions.assertTrue(result.apply(6));
        Assertions.assertFalse(result.apply(5));
    }

    @Test
    void testAnd() {
        Predicate<Integer> p1 = x -> x % 2 == 0;
        Predicate<Integer> p2 = x -> x % 3 == 0;
        Predicate<Integer> result = p1.and(p2);
        Assertions.assertTrue(result.apply(6));
        Assertions.assertFalse(result.apply(2));
        Assertions.assertFalse(result.apply(3));
        Assertions.assertFalse(result.apply(1));
    }

    @Test
    void testAndAlwaysTrue() {
        Predicate<Integer> p1 = x -> x % 2 == 0;
        Predicate<Integer> result = p1.and(Predicate.ALWAYS_TRUE());
        Assertions.assertTrue(result.apply(6));
        Assertions.assertTrue(result.apply(2));
        Assertions.assertFalse(result.apply(3));
        Assertions.assertFalse(result.apply(1));
    }

    @Test
    void testAndAlwaysFalse() {
        Predicate<Integer> p1 = x -> x % 2 == 0;
        Predicate<Integer> result = p1.and(Predicate.ALWAYS_FALSE());
        Assertions.assertFalse(result.apply(6));
        Assertions.assertFalse(result.apply(2));
        Assertions.assertFalse(result.apply(3));
    }

    @Test
    void testAndStress() {
        Predicate<Integer> p = x -> x % 2 == 0;
        Predicate<Integer> result = p.and(p);
        for (int i = 0; i < 1000; i++) {
            result = p.and(result);
        }
        Assertions.assertTrue(result.apply(6));
        Assertions.assertFalse(result.apply(5));
    }

    @Test
    void testNot() {
        Predicate<Integer> p1 = x -> x % 2 == 0;
        Predicate<Integer> result = p1.not();
        Assertions.assertFalse(result.apply(6));
        Assertions.assertFalse(result.apply(2));
        Assertions.assertTrue(result.apply(3));
    }

    @Test
    void testNotOr() {
        Predicate<Integer> p1 = x -> x % 2 == 0;
        Predicate<Integer> p2 = x -> x % 3 == 0;
        Predicate<Integer> p3 = p1.or(p2);
        Predicate<Integer> result = p3.not();
        Assertions.assertFalse(result.apply(6));
        Assertions.assertFalse(result.apply(2));
        Assertions.assertFalse(result.apply(3));
        Assertions.assertTrue(result.apply(1));
    }

    @Test
    void testNotAnd() {
        Predicate<Integer> p1 = x -> x % 2 == 0;
        Predicate<Integer> p2 = x -> x % 3 == 0;
        Predicate<Integer> p3 = p1.and(p2);
        Predicate<Integer> result = p3.not();
        Assertions.assertFalse(result.apply(6));
        Assertions.assertTrue(result.apply(2));
        Assertions.assertTrue(result.apply(3));
        Assertions.assertTrue(result.apply(1));
    }
}
