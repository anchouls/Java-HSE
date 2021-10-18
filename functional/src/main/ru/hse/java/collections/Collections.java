package ru.hse.java.collections;

import org.jetbrains.annotations.NotNull;
import ru.hse.java.functional.*;

import java.util.ArrayList;
import java.util.List;

public class Collections {

    public static <T, R> List<R> map(@NotNull final Function1<? super T, ? extends R> f, @NotNull final Iterable<? extends T> a) {
        List<R> result = new ArrayList<>();
        for (T element : a) {
            result.add(f.apply(element));
        }
        return result;
    }

    public static <T> List<T> filter(@NotNull final Predicate<? super T> p, @NotNull final Iterable<? extends T> a) {
        List<T> result = new ArrayList<>();
        for (T element : a) {
            if (p.apply(element)) {
                result.add(element);
            }
        }
        return result;
    }

    public static <T> List<T> takeWhile(@NotNull final Predicate<? super T> p, @NotNull final Iterable<? extends T> a) {
        List<T> result = new ArrayList<>();
        for (T element : a) {
            if (p.apply(element)) {
                result.add(element);
            } else {
                break;
            }
        }
        return result;
    }

    public static <T> List<T> takeUnless(@NotNull final Predicate<? super T> p, @NotNull final Iterable<? extends T> a) {
        return takeWhile(p.not(), a);
    }


    public static <T, R> T foldl(@NotNull final Function2<? super T, ? super R, ? extends T> f, final T init, @NotNull final Iterable<? extends R> a) {
        T result = init;
        for (R element : a) {
            result = f.apply(result, element);
        }
        return result;
    }

    public static <T, R> T foldr(@NotNull final Function2<? super R, ? super T, ? extends T> f, final T init, @NotNull final Iterable<? extends R> a) {
        List<R> arrayList = new ArrayList<>();
        T result = init;
        for (R element : a) {
            arrayList.add(element);
        }
        for (int i = arrayList.size() - 1; i >= 0; i--) {
            result = f.apply(arrayList.get(i), result);
        }
        return result;
    }

}
