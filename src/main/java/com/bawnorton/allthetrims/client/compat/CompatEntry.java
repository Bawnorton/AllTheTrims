package com.bawnorton.allthetrims.client.compat;

import com.bawnorton.allthetrims.platform.Platform;
import com.bawnorton.allthetrims.util.LazySupplier;
import java.util.Optional;
import java.util.function.Supplier;

public record CompatEntry<T>(String modid, LazySupplier<T> compatSupplier) {
    public Optional<T> getCompat() {
        return Platform.isModLoaded(modid) ? Optional.of(compatSupplier.get()) : Optional.empty();
    }

    public static <T> CompatEntry<T> of(String modid, Supplier<T> compatSupplier) {
        return new CompatEntry<>(modid, LazySupplier.of(compatSupplier));
    }
}
