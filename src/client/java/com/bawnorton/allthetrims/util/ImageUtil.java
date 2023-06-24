package com.bawnorton.allthetrims.util;

import net.minecraft.util.math.MathHelper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.*;

public abstract class ImageUtil {
    public static BufferedImage newBlankPaletteImage() {
        BufferedImage palette = new BufferedImage(8, 1, BufferedImage.TYPE_INT_RGB);
        Color colour = new Color(255, 255, 255);
        for (int x = 0; x < 8; x++) {
            palette.setRGB(x, 0, colour.getRGB());
        }
        return palette;
    }

    public static BufferedImage colourListToPaletteImage(List<Color> colours) {
        BufferedImage palette = new BufferedImage(colours.size(), 1, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < colours.size(); x++) {
            palette.setRGB(x, 0, colours.get(x).getRGB());
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

    public static Color getAverageColour(List<Color> colours) {
        int red = 0;
        int green = 0;
        int blue = 0;
        for(Color colour : colours) {
            red += colour.getRed();
            green += colour.getGreen();
            blue += colour.getBlue();
        }
        return new Color(red / colours.size(), green / colours.size(), blue / colours.size());
    }

    public static Color getNthDarkestColour(BufferedImage bufferedImage, int index) {
        int[] colours = bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null, 0, bufferedImage.getWidth());
        Set<Color> uniqueColours = new HashSet<>();
        for (int colour : colours) {
            uniqueColours.add(new Color(colour, true));
        }
        List<Color> sortedColours = new ArrayList<>(uniqueColours);
        sortedColours.sort(Comparator.comparingInt(Color::getRGB));
        if(index >= sortedColours.size()) index = sortedColours.size() - 1;
        return sortedColours.get(index);
    }

    public static BufferedImage removeOtherColours(BufferedImage bufferedImage, Color includeColour) {
        BufferedImage maskedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for(int x = 0; x < bufferedImage.getWidth(); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                Color colour = new Color(bufferedImage.getRGB(x, y), true);
                if (colour.equals(includeColour)) {
                    maskedImage.setRGB(x, y, colour.getRGB());
                }
            }
        }
        return maskedImage;
    }

    public static Color changeBrightness(Color color, float percent) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        float brightness = MathHelper.clamp(hsb[2] * percent, 0, 1);
        return Color.getHSBColor(hsb[0], hsb[1], brightness);
    }
}
