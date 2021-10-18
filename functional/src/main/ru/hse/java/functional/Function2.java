package ru.hse.java.functional;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Function2<T1, T2, R> {
    R apply(T1 t1, T2 t2);

    default <V> Function2<T1, T2, V> compose(@NotNull final Function1<? super R, ? extends V> g) {
        return (v, u) -> g.apply(apply(v, u));
    }

    default <F extends T1> Function1<T2, R> bind1(final F first) {
        return (v) -> apply(first, v);
    }

    default <F extends T2> Function1<T1, R> bind2(final F second) {
        return (v) -> apply(v, second);
    }

    default Function1<T1, Function1<T2, R>> curry() {
        return v -> u -> apply(v, u);
    }

}
