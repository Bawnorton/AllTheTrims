package com.bawnorton.allthetrims.util;

import java.util.function.Function;

public final class Memoizer {
    public static <T, R> MemoizedFunction<T, R> memoize(Function<T, R> function) {
        return new MemoizedFunction<>(function);
    }
}
