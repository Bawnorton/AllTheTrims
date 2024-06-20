package com.bawnorton.allthetrims.client.palette;

import com.bawnorton.allthetrims.AllTheTrims;
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
    private final List<Integer> staticColours;
    private final List<Integer> animatedColours;
    private long lastCycle;
    private int[] colourArr;

    public TrimPalette(List<Integer> colours) {
        if (colours.size() != PALETTE_SIZE) {
            throw new IllegalArgumentException("Trim palette requires exactly %s colours, but %s were found.".formatted(PALETTE_SIZE, colours.size()));
        }
        this.staticColours = colours;
        this.animatedColours = new ArrayList<>(colours);
        this.colourArr = computeArr();
    }

    public TrimPalette(int singleColour) {
        this(Util.make(new ArrayList<>(), colours -> {
            for (int i = 0; i < PALETTE_SIZE; i++) {
                colours.add(singleColour);
            }
        }));
    }

    private int[] computeArr() {
        List<Integer> reversed = getColours().reversed();
        int[] arr = new int[PALETTE_SIZE];
        for (int i = 0; i < PALETTE_SIZE; i++) {
            int colour = reversed.get(i);
            arr[i] = colour;
        }
        return arr;
    }

    public BufferedImage toBufferedImage() {
        BufferedImage image = new BufferedImage(PALETTE_SIZE, 1, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < PALETTE_SIZE; i++) {
            image.setRGB(i, 0, (255 << 24) | staticColours.get(i));
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

    public int[] getColourArr() {
        return colourArr;
    }

    public void recomputeColourArr() {
        this.colourArr = computeArr();
    }

    public List<Integer> getColours() {
        return AllTheTrims.getConfig().animate ? animatedColours : staticColours;
    }

    public void cycleAnimatedColours() {
        if (System.currentTimeMillis() - lastCycle <= AllTheTrims.getConfig().timeBetweenCycles) return;

        int last = animatedColours.getLast();
        for (int i = animatedColours.size() - 1; i > 0; i--) {
            animatedColours.set(i, animatedColours.get(i - 1));
        }
        animatedColours.set(0, last);
        recomputeColourArr();
        lastCycle = System.currentTimeMillis();
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
        return Objects.equals(this.staticColours, that.staticColours);
    }

    @Override
    public int hashCode() {
        return Objects.hash(staticColours);
    }
}
