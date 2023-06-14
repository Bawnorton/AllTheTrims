package com.bawnorton.allthetrims.util;

import net.minecraft.client.texture.NativeImage;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageUtil {
    public static final BufferedImage BLANK_PALETTE = new BufferedImage(8, 1, BufferedImage.TYPE_INT_RGB);

    public static BufferedImage getBlankPalette() {
        BLANK_PALETTE.setRGB(0, 0, 0xFFFFFF);
        BLANK_PALETTE.setRGB(1, 0, 0xDDDDDD);
        BLANK_PALETTE.setRGB(2, 0, 0xBBBBBB);
        BLANK_PALETTE.setRGB(3, 0, 0x999999);
        BLANK_PALETTE.setRGB(4, 0, 0x777777);
        BLANK_PALETTE.setRGB(5, 0, 0x555555);
        BLANK_PALETTE.setRGB(6, 0, 0x333333);
        BLANK_PALETTE.setRGB(7, 0, 0x111111);
        return BLANK_PALETTE;
    }

    public static BufferedImage convertNativeToPaletteImg(NativeImage image) {
        BufferedImage bufferedImage = new BufferedImage(8, 1, BufferedImage.TYPE_INT_RGB);
        Color averageColour = averageColour(image, 0, 0, image.getWidth(), image.getHeight());
        for(int x = 0; x < 7; x++) {
            bufferedImage.setRGB(x, 0, averageColour.getRGB());
        }
        return bufferedImage;
    }

    public static Color averageColour(NativeImage bi, int x0, int y0, int w, int h) {
        int x1 = x0 + w;
        int y1 = y0 + h;
        long sumr = 0, sumg = 0, sumb = 0;
        for (int x = x0; x < x1; x++) {
            for (int y = y0; y < y1; y++) {
                Color pixel = new Color(bi.getColor(x, y));
                sumr += pixel.getRed();
                sumg += pixel.getGreen();
                sumb += pixel.getBlue();
            }
        }
        int num = w * h;
        return new Color((float) (sumr / num) / 255, ((float) sumg / num) / 255, ((float) sumb / num) / 255);
    }
}
