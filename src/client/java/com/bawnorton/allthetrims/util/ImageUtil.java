package com.bawnorton.allthetrims.util;

import com.bawnorton.allthetrims.AllTheTrims;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.item.Item;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;

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
        Block block = Block.getBlockFromItem(item);
        if (block != null) {
            Color blockColour = getAverageColour(block);
            if(blockColour != null) {
                AVERAGE_TEXTURE_COLOUR_CACHE.put(item, blockColour);
                return blockColour;
            }
        }

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

//        long[] colourVals = new long[]{0, 0, 0, 0};
//        int size = 0;
//        for (int x = 0; x < content.getWidth(); x++) {
//            for (int y = 0; y < content.getHeight(); y++) {
//                int pixel = content.image.getColor(x, y);
//                int borrowedAlpha = pixel >> 24 & 0xFF;
//                if (borrowedAlpha >= 5) {
//                    colourVals[0] += borrowedAlpha;
//                    colourVals[1] += (pixel & 0xFF);
//                    colourVals[2] += (pixel >> 8 & 0xFF);
//                    colourVals[3] += (pixel >> 16 & 0xFF);
//                    size++;
//                }
//            }
//        }
//        for (int i = 0; i < colourVals.length; i++) {
//            colourVals[i] = Math.round((float) colourVals[i] / size);
//            colourVals[i] = colourVals[i] >= 255 ? 255 : colourVals[i];
//        }
//        int colourValue = ColorHelper.Argb.getArgb(255, (int) colourVals[1], (int) colourVals[2], (int) colourVals[3]);
//
//        Color colour = new Color(colourValue);
        Color colour = getColourFromSpriteContent(content);
        AVERAGE_TEXTURE_COLOUR_CACHE.put(item, colour);
        return colour;
    }

    private static @Nullable Color getAverageColour(Block block) {
        BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
        BlockModels models = blockRenderManager.getModels();
        BakedModel model = models.getModel(block.getDefaultState());
        if (model == null) {
            AllTheTrims.LOGGER.warn("Block " + block.getName().getString() + " has no model, trying item model");
            return null;
        }

        Sprite sprite = model.getParticleSprite();
        if (sprite == null) {
            AllTheTrims.LOGGER.warn("Model of block " + block.getName().getString() + " has no particle sprite, trying item model");
            return null;
        }

        SpriteContents content = sprite.getContents();
        if (content.getDistinctFrameCount().count() <= 0) {
            AllTheTrims.LOGGER.warn("Sprite of block " + block.getName().getString() + " has no frames, trying item model");
            return null;
        }

        return getColourFromSpriteContent(content);
    }


    private static Color getColourFromSpriteContent(SpriteContents content) {
        Map<int[], Integer> colourMap = new HashMap<>();
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
                    colourMap.put(argb, colourMap.getOrDefault(argb, 0) + 1);
                }
            }
        }
        return mergeColors(colourMap);
    }

    private static Color mergeColors(Map<int[], Integer> colourMap) {
        Map<Integer, Integer> colourFrequencyMap = new HashMap<>();

        for (int[] colour : colourMap.keySet()) {
            int frequency = colourMap.get(colour);
            colourFrequencyMap.put(Arrays.hashCode(colour), colourFrequencyMap.getOrDefault(Arrays.hashCode(colour), 0) + frequency);
        }

        List<Map.Entry<Integer, Integer>> sortedColors = new ArrayList<>(colourFrequencyMap.entrySet());
        sortedColors.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        List<Integer> mergedColors = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : sortedColors) {
            int colour = entry.getKey();
            int frequency = entry.getValue();

            boolean similarColorFound = false;
            for (int mergedColor : mergedColors) {
                if (areColorsSimilar(colour, mergedColor, 5)) {
                    int mergedFrequency = colourFrequencyMap.get(mergedColor);
                    colourFrequencyMap.put(mergedColor, mergedFrequency + frequency);
                    similarColorFound = true;
                    break;
                }
            }

            if (!similarColorFound) {
                mergedColors.add(colour);
            }
        }

        List<Integer> topColors = new ArrayList<>();
        for (int i = 0; i < Math.min(5, mergedColors.size()); i++) {
            topColors.add(mergedColors.get(i));
        }

        int alphaSum = 0;
        int redSum = 0;
        int greenSum = 0;
        int blueSum = 0;
        int totalFrequency = 0;

        for (int colour : topColors) {
            int frequency = colourFrequencyMap.get(colour);
            int alpha = (colour >> 24) & 0xFF;
            int red = (colour >> 16) & 0xFF;
            int green = (colour >> 8) & 0xFF;
            int blue = colour & 0xFF;

            alphaSum += alpha * frequency;
            redSum += red * frequency;
            greenSum += green * frequency;
            blueSum += blue * frequency;
            totalFrequency += frequency;
        }

        int averageAlpha = alphaSum / totalFrequency;
        int averageRed = redSum / totalFrequency;
        int averageGreen = greenSum / totalFrequency;
        int averageBlue = blueSum / totalFrequency;

        return argbToColour((averageAlpha << 24) | (averageRed << 16) | (averageGreen << 8) | averageBlue);
    }

    private static boolean areColorsSimilar(int colour1, int colour2, int hueRange) {
        int hue1 = calculateHue(colour1);
        int hue2 = calculateHue(colour2);
        int hueDifference = Math.abs(hue1 - hue2);

        return hueDifference <= hueRange || hueDifference >= (360 - hueRange);
    }

    private static int calculateHue(int colour) {
        int red = (colour >> 16) & 0xFF;
        int green = (colour >> 8) & 0xFF;
        int blue = colour & 0xFF;

        return (int) Math.toDegrees(Math.atan2(Math.sqrt(3) * (green - blue), 2 * red - green - blue));
    }

    private static Color argbToColour(int colourValue) {
        int alpha = (colourValue >> 24) & 0xFF;
        int red = (colourValue >> 16) & 0xFF;
        int green = (colourValue >> 8) & 0xFF;
        int blue = colourValue & 0xFF;

        return new Color(red, green, blue, alpha);
    }
}
