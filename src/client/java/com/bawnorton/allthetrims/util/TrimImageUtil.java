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

public class TrimImageUtil {
    private static final Map<Item, Color> textureCache = new HashMap<>();

    public static BufferedImage newBlankPalette() {
        BufferedImage blank = new BufferedImage(8, 1, BufferedImage.TYPE_INT_RGB);
        blank.setRGB(0, 0, 0xFFFFFF);
        blank.setRGB(1, 0, 0xEEEEEE);
        blank.setRGB(2, 0, 0xDDDDDD);
        blank.setRGB(3, 0, 0xCCCCCC);
        blank.setRGB(4, 0, 0xBBBBBB);
        blank.setRGB(5, 0, 0xAAAAAA);
        blank.setRGB(6, 0, 0x999999);
        blank.setRGB(7, 0, 0x888888);
        return blank;
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
        if(textureCache.containsKey(item)) return textureCache.get(item);
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ItemModels models = itemRenderer.getModels();
        BakedModel model = models.getModel(item);
        if(model == null) {
            AllTheTrims.LOGGER.warn("Could not find model for item " + item.getName().getString());
            return new Color(255, 255, 255);
        }

        Sprite sprite = model.getParticleSprite();
        if(sprite == null) {
            AllTheTrims.LOGGER.warn("Could not find sprite for item " + item.getName().getString());
            return new Color(255, 255, 255);
        }

        SpriteContents content = sprite.getContents();
        if (content.getDistinctFrameCount().count() <= 0) {
            AllTheTrims.LOGGER.warn("Could not find sprite content for item " + item.getName().getString());
            return new Color(255, 255, 255);
        }

        long[] colorVals = new long[]{0, 0, 0, 0};
        int size = 0;
        for (int x = 0; x < content.getWidth(); x++) {
            for (int y = 0; y < content.getHeight(); y++) {
                int pixel = content.image.getColor(x, y);
                int borrowedAlpha =  pixel >> 24 & 0xFF;
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
            colorVals[i] = i != 0 ? colorVals[i] >= 235 ? 255 : colorVals[i] + 20 : colorVals[i];
        }
        int colourValue = ColorHelper.Argb.getArgb(255, (int) colorVals[1], (int) colorVals[2], (int) colorVals[3]);
        Color colour = new Color(colourValue, true);
        textureCache.put(item, colour);
        return colour;
    }
}
