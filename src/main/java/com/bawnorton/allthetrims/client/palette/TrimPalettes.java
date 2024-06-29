package com.bawnorton.allthetrims.client.palette;

import com.bawnorton.allthetrims.client.debug.Debugger;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import org.jetbrains.annotations.Nullable;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

public final class TrimPalettes {
    private final ConcurrentMap<Item, TrimPalette> cache = new ConcurrentHashMap<>();
    private final TrimPaletteGenerator generator = new TrimPaletteGenerator();

    public TrimPalette getOrGeneratePalette(Item item) {
        return cache.computeIfAbsent(item, k -> {
            TrimPalette newPalette = generator.generatePalette(k);
            createDebugFile(k, newPalette);
            return newPalette;
        });
    }

    public @Nullable TrimPalette getPalette(Item item) {
        return cache.get(item);
    }

    public void forEach(Consumer<TrimPalette> consumer) {
        cache.values().forEach(consumer);
    }

    public void regenerate() {
        Set<Item> cached = cache.keySet();
        for (Item item : cached) {
            TrimPalette palette = generator.generatePalette(item);
            createDebugFile(item, palette);
            cache.put(item, palette);
        }
    }

    private void createDebugFile(Item item, TrimPalette palette) {
        Debugger.createImage(
                "palettes/%s.png".formatted(Registries.ITEM.getId(item)),
                palette.toBufferedImage()
        );
    }
}
