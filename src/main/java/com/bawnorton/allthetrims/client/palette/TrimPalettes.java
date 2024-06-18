package com.bawnorton.allthetrims.client.palette;

import javax.imageio.ImageIO;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class TrimPalettes {
    private final Map<Item, TrimPalette> cache = new HashMap<>();
    private final TrimPaletteGenerator generator = new TrimPaletteGenerator();

    public TrimPalette getTrimPaletteFor(Item item) {
        return cache.computeIfAbsent(item, k -> {
            TrimPalette palette = generator.generatePalette(k);
            createDebugFile(k, palette);
            return palette;
        });
    }

    private void createDebugFile(Item item, TrimPalette palette) {
        BufferedImage image = palette.toBufferedImage();
        try {
            File file = FabricLoader.getInstance().getGameDir().resolve("att-debug").resolve("%s.png".formatted(Registries.ITEM.getId(item))).toFile();
            file.mkdirs();
            file.createNewFile();
            ImageIO.write(image, "png", file);
        } catch (IOException ignored) {}
    }
}
