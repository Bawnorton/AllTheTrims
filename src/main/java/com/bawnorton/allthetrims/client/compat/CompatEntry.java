package com.bawnorton.allthetrims.client.compat;

import com.bawnorton.allthetrims.platform.Platform;
import java.util.Optional;

public record CompatEntry<T>(String modid, T compat) {
    public Optional<T> getCompat() {
        return Platform.isModLoaded(modid) ? Optional.of(compat) : Optional.empty();
    }

    public static <T> CompatEntry<T> of(String modid, T compat) {
        return new CompatEntry<>(modid, compat);
    }
}
