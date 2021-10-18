package ru.hse.java.test.functional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.hse.java.functional.Function1;
import ru.hse.java.functional.Function2;

public class Function2Tests {
    @Test
    void testCompose() {
        Function2<Integer, Integer, Integer> f = (x, y) -> x * y;
        Function1<Integer, Integer> g = x -> x + 2;
        Assertions.assertEquals(8, f.compose(g).apply(2, 3));
        Assertions.assertEquals(3, f.compose(g).apply(1, 1));
        Assertions.assertEquals(2, f.compose(g).apply(0, 100));
    }

    @Test
    void testComposeWithDifferentTypes() {
        Function2<Integer, String, String> f = (x, y) -> x.toString() + y;
        Function1<String, String> g = x -> x + "6";
        Assertions.assertEquals("5+6", f.compose(g).apply(5, "+"));
        Assertions.assertEquals("100-6", f.compose(g).apply(100, "-"));
        Assertions.assertEquals("-32*6", f.compose(g).apply(-32, "*"));
    }

    @Test
    void testBind1() {
        Function2<Integer, String, String> f = (x, y) -> x.toString() + y;
        Function1<String, String> g = f.bind1(5);
        Assertions.assertEquals("5.", g.apply("."));
        Assertions.assertEquals("5?", g.apply("?"));
        Assertions.assertEquals("5...", g.apply("..."));
    }

    @Test
    void testBind1AndCompose() {
        Function2<Integer, Integer, Integer> f = (x, y) -> x * y;
        Function1<Integer, Integer> g = x -> x + 2;
        Function1<Integer, Integer> h = f.bind1(5);
        Assertions.assertEquals(35, g.compose(h).apply(5));
        Assertions.assertEquals(25, g.compose(h).apply(3));
        Assertions.assertEquals(10, g.compose(h).apply(0));
    }

    @Test
    void testBind2() {
        Function2<Integer, String, String> f = (x, y) -> x.toString() + y;
        Function1<Integer, String> g = f.bind2(".");
        Assertions.assertEquals("5.", g.apply(5));
        Assertions.assertEquals("10.", g.apply(10));
        Assertions.assertEquals("1000.", g.apply(1000));
    }

    @Test
    void testBind2AndCompose() {
        Function2<Integer, Integer, Integer> f = (x, y) -> x * y;
        Function1<Integer, Integer> g = x -> x + 2;
        Function1<Integer, Integer> h = f.bind2(5);
        Assertions.assertEquals(35, g.compose(h).apply(5));
        Assertions.assertEquals(25, g.compose(h).apply(3));
        Assertions.assertEquals(10, g.compose(h).apply(0));
    }

    @Test
    void testBind1And2() {
        Function2<Integer, String, String> f = (x, y) -> x.toString() + y;
        Function1<Integer, String> g1 = f.bind2(".");
        Function1<String, String> g2 = f.bind1(5);
        Assertions.assertEquals(g1.apply(5), g2.apply("."));
    }

    @Test
    void testCurry() {
        Function2<Integer, String, String> f = (x, y) -> x.toString() + y;
        Function1<Integer, Function1<String, String>> g = f.curry();
        Assertions.assertEquals(f.apply(1, "@"), g.apply(1).apply("@"));
        Assertions.assertEquals(f.apply(172, "271"), g.apply(172).apply("271"));
        Assertions.assertEquals(f.apply(8, "&&&"), g.apply(8).apply("&&&"));
    }

    @Test
    void testCurryStress() {
        Function2<Integer, String, String> f = (x, y) -> x.toString() + y;
        Function1<Integer, Function1<String, String>> g = f.curry();
        for (int i = 0; i < 1000; i++) {
            Assertions.assertEquals(f.apply(i, Integer.toString(i)), g.apply(i).apply(Integer.toString(i)));
        }
    }
}
