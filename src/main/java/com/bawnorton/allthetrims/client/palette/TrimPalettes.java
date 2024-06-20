package com.bawnorton.allthetrims.client.palette;

import com.bawnorton.allthetrims.client.debug.Debugger;
import javax.imageio.ImageIO;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public final class TrimPalettes {
    private final Map<Item, TrimPalette> cache = new HashMap<>();
    private final TrimPaletteGenerator generator = new TrimPaletteGenerator();

    public TrimPalette getTrimPaletteFor(Item item) {
        return cache.computeIfAbsent(item, k -> {
            TrimPalette newPalette = generator.generatePalette(k);
            createDebugFile(k, newPalette);
            return newPalette;
        });
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
