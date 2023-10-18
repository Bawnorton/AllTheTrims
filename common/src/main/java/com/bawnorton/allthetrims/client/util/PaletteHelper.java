package com.bawnorton.allthetrims.client.util;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.util.DebugHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.ColorHelper;

import java.awt.*;
import java.util.List;
import java.util.*;

public abstract class PaletteHelper {
    public static final List<Color> WHITE_PALETTE;
    private static final Map<Identifier, List<Color>> PALETTES = new HashMap<>();
    private static final List<Color> BLANK_PALETTE;

    static {
        BLANK_PALETTE = new ArrayList<>();
        WHITE_PALETTE = DefaultedList.ofSize(8, new Color(255, 255, 255));
        for (int i = 8; i > 0; i--) {
            BLANK_PALETTE.add(new Color(255 - 255 / i, 255 - 255 / i, 255 - 255 / i));
        }
    }

    public static boolean paletteExists(Identifier identifier) {
        return PALETTES.containsKey(identifier);
    }

    public static List<Color> getPalette(Item item) {
        return getPalette(Registries.ITEM.getId(item));
    }

    public static List<Color> getPalette(Identifier identifier) {
        if (PALETTES.containsKey(identifier)) return PALETTES.get(identifier);

        Item item = Registries.ITEM.get(identifier);
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ItemModels models = itemRenderer.getModels();
        BakedModel model = models.getModel(item);
        if (model == null) {
            AllTheTrims.LOGGER.warn("Item " + item.getName().getString() + " has no model, using blank palette");
            putPalette(identifier, BLANK_PALETTE);
            return BLANK_PALETTE;
        }

        Sprite sprite = model.getParticleSprite();
        if (sprite == null) {
            AllTheTrims.LOGGER.warn("Model of item " + item.getName()
                .getString() + " has no particle sprite, using blank palette");
            putPalette(identifier, BLANK_PALETTE);
            return BLANK_PALETTE;
        }

        SpriteContents content = sprite.getContents();
        if (content.getDistinctFrameCount().count() <= 0) {
            AllTheTrims.LOGGER.warn("Sprite of item " + item.getName()
                .getString() + " has no frames, using blank palette");
            putPalette(identifier, BLANK_PALETTE);
            return BLANK_PALETTE;
        }

        putPalette(identifier, PaletteGenerator.generatePalette(content.image));
        return PALETTES.get(identifier);
    }

    public static void putPalette(Identifier identifier, List<Color> palette) {
        DebugHelper.savePalette(ImageUtil.colourListToPaletteImage(palette), identifier + ".png");
        PALETTES.put(identifier, palette);
    }

    public static List<Color> existingResourceToPalette(Resource resource) {
        try {
            return PaletteGenerator.toPalette(NativeImage.read(resource.getInputStream()));
        } catch (Exception e) {
            AllTheTrims.LOGGER.error("Failed to read palette image", e);
            return BLANK_PALETTE;
        }
    }

    private abstract static class PaletteGenerator {
        public static List<Color> generatePalette(NativeImage image) {
            Map<Color, Integer> colourMap = new HashMap<>();
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    int pixel = image.getColor(x, y);
                    int a = pixel >> 24 & 0xFF;
                    if (a >= 5) {
                        Color colour = extractColour(pixel, a);
                        colourMap.put(colour, colourMap.getOrDefault(colour, 0) + 1);
                    }
                }
            }

            return coloursToPalette(removeGreys(new ArrayList<>(colourMap.keySet())));
        }

        private static List<Color> removeGreys(List<Color> colours) {
            List<Color> greys = new ArrayList<>();
            for (Color colour : colours) {
                float[] hsb = Color.RGBtoHSB(colour.getRed(), colour.getGreen(), colour.getBlue(), null);
                if (hsb[1] < 0.2) {
                    greys.add(colour);
                }
            }
            colours.removeAll(greys);
            if (colours.isEmpty()) return greys;
            return colours;
        }

        private static List<Color> coloursToPalette(List<Color> colours) {
            colours.sort(Comparator.comparing((color -> {
                float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
                return hsb[1];
            })));
            Color darkest = colours.get(0);
            darkest = ImageUtil.changeBrightness(darkest, 0.95f);
            Color lightest = colours.get(colours.size() - 1);
            lightest = ImageUtil.changeBrightness(lightest, 1.05f);
            return stretchColors(darkest, lightest);
        }

        private static List<Color> stretchColors(Color... originalColors) {
            List<Color> stretchedColors = new ArrayList<>();

            int segmentCount = 7;
            double segmentSize = (double) segmentCount / (originalColors.length - 1);
            for (int i = 0; i < originalColors.length - 1; i++) {
                Color startColor = originalColors[i];
                Color endColor = originalColors[i + 1];
                for (int j = 0; j < segmentSize; j++) {
                    float ratio = (float) j / (float) segmentSize;
                    int red = (int) (startColor.getRed() + ratio * (endColor.getRed() - startColor.getRed()));
                    int green = (int) (startColor.getGreen() + ratio * (endColor.getGreen() - startColor.getGreen()));
                    int blue = (int) (startColor.getBlue() + ratio * (endColor.getBlue() - startColor.getBlue()));
                    stretchedColors.add(new Color(red, green, blue));
                }
            }

            stretchedColors.add(originalColors[originalColors.length - 1]);
            return stretchedColors;
        }

        public static List<Color> toPalette(NativeImage image) {
            if (image.getWidth() != 8 || image.getHeight() != 1) {
                AllTheTrims.LOGGER.warn("Palette image is not 8x1 pixels, using blank palette");
                return BLANK_PALETTE;
            }

            List<Color> palette = new ArrayList<>();
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = image.getColor(x, 0);
                int a = pixel >> 24 & 0xFF;
                if (a >= 5) {
                    Color colour = extractColour(pixel, a);
                    palette.add(colour);
                }
            }
            return palette;
        }

        private static Color extractColour(int pixel, int alpha) {
            int[] argb = new int[4];
            argb[0] = alpha;
            argb[1] = (pixel & 0xFF);
            argb[2] = (pixel >> 8 & 0xFF);
            argb[3] = (pixel >> 16 & 0xFF);
            return new Color(ColorHelper.Argb.getArgb(argb[0], argb[1], argb[2], argb[3]), true);
        }
    }
}
