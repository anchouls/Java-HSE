package ru.hse.java.functional;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Predicate<T> extends Function1<T, Boolean> {

    static <T> Predicate<T> ALWAYS_TRUE() {
        return v -> true;
    }

    static <T> Predicate<T> ALWAYS_FALSE() {
        return v -> false;
    }

    default Predicate<T> or(@NotNull final Predicate<? super T> o) {
        return v -> apply(v) || o.apply(v);
    }

    default Predicate<T> and(@NotNull final Predicate<? super T> o) {
        return v -> apply(v) && o.apply(v);
    }

    default Predicate<T> not() {
        return v -> !apply(v);
    }

}
