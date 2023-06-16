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
import java.util.HashMap;
import java.util.Map;

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

    public static Color getMedianColour(Item item) {
        if (AVERAGE_TEXTURE_COLOUR_CACHE.containsKey(item)) return AVERAGE_TEXTURE_COLOUR_CACHE.get(item);
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ItemModels models = itemRenderer.getModels();
        BakedModel model = models.getModel(item);
        if (model == null) {
            AllTheTrims.LOGGER.warn("Item " + item.getName().getString() + " has no model");
            Color colour = new Color(255, 255, 255);
            AVERAGE_TEXTURE_COLOUR_CACHE.put(item, colour);
            return colour;
        }

        Sprite sprite = model.getParticleSprite();
        if (sprite == null) {
            AllTheTrims.LOGGER.warn("Model of item " + item.getName().getString() + " has no particle sprite");
            Color colour = new Color(255, 255, 255);
            AVERAGE_TEXTURE_COLOUR_CACHE.put(item, colour);
            return colour;
        }

        SpriteContents content = sprite.getContents();
        if (content.getDistinctFrameCount().count() <= 0) {
            AllTheTrims.LOGGER.warn("Sprite of item " + item.getName().getString() + " has no frames");
            Color colour = new Color(255, 255, 255);
            AVERAGE_TEXTURE_COLOUR_CACHE.put(item, colour);
            return colour;
        }

        long[] colorVals = new long[]{0, 0, 0, 0};
        int size = 0;
        for (int x = 0; x < content.getWidth(); x++) {
            for (int y = 0; y < content.getHeight(); y++) {
                int pixel = content.image.getColor(x, y);
                int borrowedAlpha = pixel >> 24 & 0xFF;
                if (borrowedAlpha >= 5) {
                    colorVals[0] += borrowedAlpha;
                    colorVals[1] += (pixel & 0xFF);
                    colorVals[2] += (pixel >> 8 & 0xFF);
                    colorVals[3] += (pixel >> 16 & 0xFF);
                    size++;
                }
            }
        }
        for (int i = 0; i < colorVals.length; i++) {
            colorVals[i] = Math.round((float) colorVals[i] / size);
            colorVals[i] = colorVals[i] >= 255 ? 255 : colorVals[i];
        }
        int colourValue = ColorHelper.Argb.getArgb(255, (int) colorVals[1], (int) colorVals[2], (int) colorVals[3]);

        Color colour = new Color(colourValue);
        AVERAGE_TEXTURE_COLOUR_CACHE.put(item, colour);
        return colour;
    }
}
