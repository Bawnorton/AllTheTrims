package com.bawnorton.allthetrims.util;

import java.util.function.Supplier;

public final class LazySupplier<T> implements Supplier<T> {
    private final Supplier<T> supplier;
    private T value;

    public LazySupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public static <T> LazySupplier<T> of(Supplier<T> supplier) {
        return new LazySupplier<>(supplier);
    }

    @Override
    public T get() {
        if (value != null) return value;

        value = supplier.get();
        return value;
    }
}
