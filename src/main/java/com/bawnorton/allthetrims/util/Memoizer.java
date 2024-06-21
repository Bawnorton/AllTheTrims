package com.bawnorton.allthetrims.util;

import java.util.function.BiFunction;
import java.util.function.Function;

public final class Memoizer {
    public static <T, R> MemoizedFunction<T, R> memoize(Function<T, R> function) {
        return new MemoizedFunction<>(function);
    }

    public static <T, K, R> MemoizedBiFunction<T, K, R> memoize(BiFunction<T, K, R> function) {
        return new MemoizedBiFunction<>(function);
    }
}
