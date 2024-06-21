package com.bawnorton.allthetrims.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class MemoizedBiFunction<T, U, R> implements BiFunction<T, U, R> {
    private final Map<Key<T, U>, R> cache = new ConcurrentHashMap<>();
    private final BiFunction<T, U, R> function;

    public MemoizedBiFunction(BiFunction<T, U, R> function) {
        this.function = function;
    }

    public R apply(T t, U u) {
        return cache.computeIfAbsent(new Key<>(t, u), k -> function.apply(t, u));
    }

    public void clear() {
        cache.clear();
    }

    private record Key<T, U>(T t, U u) {}
}
