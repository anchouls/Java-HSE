package ru.hse.java.test.functional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.hse.java.functional.Function1;

public class Function1Tests {
    @Test
    void testCompose() {
        Function1<Integer, Integer> f = x -> x * 2;
        Function1<Integer, Integer> g = x -> x + 2;
        Function1<Integer, Integer> result = f.compose(g);
        Assertions.assertEquals(4, result.apply(1));
        Assertions.assertEquals(2, result.apply(0));
        Assertions.assertEquals(0, result.apply(-1));
    }

    @Test
    void testComposeInDifferentOrder() {
        Function1<Integer, Integer> f = x -> x * 2;
        Function1<Integer, Integer> g = x -> x + 2;
        Function1<Integer, Integer> result = g.compose(f);
        Assertions.assertEquals(6, result.apply(1));
        Assertions.assertEquals(4, result.apply(0));
        Assertions.assertEquals(2, result.apply(-1));
    }

    @Test
    void testSymmetricCompose() {
        Function1<Integer, Integer> f = x -> x + 2;
        Function1<Integer, Integer> g = x -> x + 2;
        Function1<Integer, Integer> result1 = f.compose(g);
        Function1<Integer, Integer> result2 = g.compose(f);
        Assertions.assertEquals(result1.apply(1), result2.apply(1));
        Assertions.assertNotSame(result1.apply(0), result2.apply(1));
    }

    @Test
    void testComposeWithDifferentTypes() {
        Function1<Integer, Integer> f = x -> x + 2;
        Function1<Integer, String> g = Object::toString;
        Function1<Integer, String> result = f.compose(g);
        Assertions.assertEquals("3", result.apply(1));
        Assertions.assertEquals("2", result.apply(0));
        Assertions.assertEquals("1", result.apply(-1));
    }

    @Test
    void testComposeStress() {
        Function1<Integer, Integer> f = x -> x + 2;
        Function1<Integer, Integer> result = f.compose(f);
        for (int i = 0; i < 1000; i++) {
            result = f.compose(result);
        }
        Assertions.assertEquals(2004, result.apply(0));
    }
}
