package com.bawnorton.allthetrims.client.palette;

import javax.imageio.ImageIO;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class TrimPalette {
    public static final TrimPalette DEFAULT = new TrimPalette(ColorHelper.Argb.getArgb(255, 255, 255));
    public static final int PALETTE_SIZE = 8;
    private final List<Integer> colours;
    private final float[] colourArr;

    public TrimPalette(List<Integer> colours) {
        if (colours.size() != PALETTE_SIZE) {
            throw new IllegalArgumentException("Trim palette requires exactly %s colours".formatted(PALETTE_SIZE));
        }
        this.colours = colours;
        this.colourArr = new float[PALETTE_SIZE * 3];
        for (int i = 0; i < PALETTE_SIZE; i++) {
            int colour = colours.get(i);
            int red = ColorHelper.Argb.getRed(colour);
            int green = ColorHelper.Argb.getGreen(colour);
            int blue = ColorHelper.Argb.getBlue(colour);

            colourArr[i * 3] = red / 255f;
            colourArr[i * 3 + 1] = green / 255f;
            colourArr[i * 3 + 2] = blue / 255f;
        }
    }

    public TrimPalette(int singleColour) {
        this(Util.make(new ArrayList<>(), colours -> {
            for (int i = 0; i < PALETTE_SIZE; i++) {
                colours.add(singleColour);
            }
        }));
    }

    public BufferedImage toBufferedImage() {
        BufferedImage image = new BufferedImage(PALETTE_SIZE, 1, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < PALETTE_SIZE; i++) {
            image.setRGB(i, 0, (255 << 24) | colours.get(i));
        }
        return image;
    }

    public InputStream toInputStream() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            BufferedImage bufferedImage = toBufferedImage();
            ImageIO.write(bufferedImage, "png", baos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }

    public float[] getFloatArray() {
        return colourArr;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (TrimPalette) obj;
        return Objects.equals(this.colours, that.colours);
    }

    @Override
    public int hashCode() {
        return Objects.hash(colours);
    }
}
