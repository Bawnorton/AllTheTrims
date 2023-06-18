package com.bawnorton.allthetrims.util;

import com.bawnorton.allthetrims.AllTheTrims;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.item.Item;
import net.minecraft.util.math.ColorHelper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.*;

public class ImageUtil {
    private static final Map<Item, Color> AVERAGE_TEXTURE_COLOUR_CACHE = new HashMap<>();

    public static BufferedImage newBlankPalette() {
        return newColouredPalette(new Color(255, 255, 255));
    }

    public static BufferedImage newColouredPalette(Color colour) {
        BufferedImage palette = new BufferedImage(8, 1, BufferedImage.TYPE_INT_RGB);
        int fadeAmount = 0x1A;
        for (int x = 0; x < 8; x++) {
            palette.setRGB(x, 0, colour.getRGB());
            colour = new Color(Math.max(colour.getRed() - fadeAmount, 0), Math.max(colour.getGreen() - fadeAmount, 0), Math.max(colour.getBlue() - fadeAmount, 0));
        }
        return palette;
    }

    public static InputStream toInputStream(BufferedImage image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }

    public static Color getAverageColour(Item item) {
        if (AVERAGE_TEXTURE_COLOUR_CACHE.containsKey(item)) return AVERAGE_TEXTURE_COLOUR_CACHE.get(item);
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ItemModels models = itemRenderer.getModels();
        BakedModel model = models.getModel(item);
        if (model == null) {
            AllTheTrims.LOGGER.warn("Item " + item.getName().getString() + " has no model, using white");
            Color colour = new Color(255, 255, 255);
            AVERAGE_TEXTURE_COLOUR_CACHE.put(item, colour);
            return colour;
        }

        Sprite sprite = model.getParticleSprite();
        if (sprite == null) {
            AllTheTrims.LOGGER.warn("Model of item " + item.getName().getString() + " has no particle sprite, using white");
            Color colour = new Color(255, 255, 255);
            AVERAGE_TEXTURE_COLOUR_CACHE.put(item, colour);
            return colour;
        }

        SpriteContents content = sprite.getContents();
        if (content.getDistinctFrameCount().count() <= 0) {
            AllTheTrims.LOGGER.warn("Sprite of item " + item.getName().getString() + " has no frames, using white");
            Color colour = new Color(255, 255, 255);
            AVERAGE_TEXTURE_COLOUR_CACHE.put(item, colour);
            return colour;
        }

        Color colour = getColourFromSpriteContent(content);
        AVERAGE_TEXTURE_COLOUR_CACHE.put(item, colour);
        return colour;
    }

    private static Color getColourFromSpriteContent(SpriteContents content) {
        Map<Color, Integer> colourMap = new HashMap<>();
        for (int x = 0; x < content.getWidth(); x++) {
            for (int y = 0; y < content.getHeight(); y++) {
                int pixel = content.image.getColor(x, y);
                int a = pixel >> 24 & 0xFF;
                if (a >= 5) {
                    int[] argb = new int[4];
                    argb[0] = a;
                    argb[1] = (pixel & 0xFF);
                    argb[2] = (pixel >> 8 & 0xFF);
                    argb[3] = (pixel >> 16 & 0xFF);
                    Color colour = new Color(ColorHelper.Argb.getArgb(argb[0], argb[1], argb[2], argb[3]), true);
                    colourMap.put(colour, colourMap.getOrDefault(colour, 0) + 1);
                }
            }
        }
        return mergeAndAverageColors(colourMap, 10);
    }

    public static Color mergeAndAverageColors(Map<Color, Integer> colorMap, int hueRange) {
        Map<Color, Integer> mergedColorMap = new HashMap<>();
        for (Map.Entry<Color, Integer> entry : colorMap.entrySet()) {
            Color color = entry.getKey();
            int frequency = entry.getValue();
            boolean merged = false;

            for (Color mergedColor : mergedColorMap.keySet()) {
                if (isColorSimilar(color, mergedColor, hueRange)) {
                    int mergedFrequency = mergedColorMap.get(mergedColor);
                    Color normalMerged = normalMerge(mergedColor, mergedFrequency, color, frequency);
                    mergedColorMap.put(normalMerged, mergedFrequency + frequency);
                    merged = true;
                    break;
                }
            }

            if (!merged) {
                mergedColorMap.put(color, frequency);
            }
        }

        List<Map.Entry<Color, Integer>> sortedColors = new ArrayList<>(mergedColorMap.entrySet());
        sortedColors.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        List<Color> topColors = new ArrayList<>();
        for (int i = 0; i < Math.min(5, sortedColors.size()); i++) {
            topColors.add(sortedColors.get(i).getKey());
        }

        return calculateAverageColor(topColors);
    }

    private static boolean isColorSimilar(Color color1, Color color2, int hueRange) {
        float[] hsb1 = Color.RGBtoHSB(color1.getRed(), color1.getGreen(), color1.getBlue(), null);
        float[] hsb2 = Color.RGBtoHSB(color2.getRed(), color2.getGreen(), color2.getBlue(), null);
        float hueDiff = Math.abs(hsb1[0] - hsb2[0]);
        hueDiff = Math.min(hueDiff, 1 - hueDiff);
        float hueRangeNormalized = (float) hueRange / 360;
        return hueDiff <= hueRangeNormalized;
    }

    private static Color calculateAverageColor(List<Color> colors) {
        int redSum = 0;
        int greenSum = 0;
        int blueSum = 0;

        for (Color color : colors) {
            redSum += color.getRed();
            greenSum += color.getGreen();
            blueSum += color.getBlue();
        }

        int averageRed = redSum / colors.size();
        int averageGreen = greenSum / colors.size();
        int averageBlue = blueSum / colors.size();

        return new Color(averageRed, averageGreen, averageBlue);
    }

    public static Color normalMerge(Color color1, int frequency1, Color color2, int frequency2) {
        double weight1 = frequency1 / (double) (frequency1 + frequency2);
        double weight2 = frequency2 / (double) (frequency1 + frequency2);

        int red1 = color1.getRed();
        int green1 = color1.getGreen();
        int blue1 = color1.getBlue();

        int red2 = color2.getRed();
        int green2 = color2.getGreen();
        int blue2 = color2.getBlue();

        int mergedRed = (int) (weight1 * red1 + weight2 * red2);
        int mergedGreen = (int) (weight1 * green1 + weight2 * green2);
        int mergedBlue = (int) (weight1 * blue1 + weight2 * blue2);

        return new Color(mergedRed, mergedGreen, mergedBlue);
    }
}
