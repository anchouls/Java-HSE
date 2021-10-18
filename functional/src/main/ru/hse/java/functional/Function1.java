package ru.hse.java.functional;


import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Function1<T, R> {
    R apply(T t);

    default <V> Function1<T, V> compose(@NotNull final Function1<? super R, ? extends V> g) {
        return v -> g.apply(apply(v));
    }
}

