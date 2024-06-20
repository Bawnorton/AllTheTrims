package com.bawnorton.allthetrims.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class MemoizedFunction<T, R> implements Function<T, R> {
    private final Map<T, R> cache = new ConcurrentHashMap<>();
    private final Function<T, R> function;

    public MemoizedFunction(Function<T, R> function) {
        this.function = function;
    }

    public R apply(T t) {
        return cache.computeIfAbsent(t, function);
    }

    public void clear() {
        cache.clear();
    }
}
