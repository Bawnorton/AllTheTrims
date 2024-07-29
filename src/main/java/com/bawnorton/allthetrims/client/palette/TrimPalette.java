package com.bawnorton.allthetrims.client.palette;

import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import com.bawnorton.allthetrims.client.colour.ColourInterpolation;
import com.bawnorton.allthetrims.client.colour.OkLabHelper;
import com.bawnorton.allthetrims.client.config.Config;
import com.bawnorton.allthetrims.versioned.VLists;
import javax.imageio.ImageIO;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class TrimPalette {
    public static final TrimPalette DEFAULT = new TrimPalette(ColorHelper.Argb.getArgb(255, 255, 255, 255));
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
        this.animatedColours = new ArrayList<>();

        Config config = AllTheTrimsClient.getConfig();
        computeInterpolation(config.animationInterpolation);
    }

    public TrimPalette(int singleColour) {
        this(Util.make(new ArrayList<>(), colours -> {
            for (int i = 0; i < PALETTE_SIZE; i++) {
                colours.add(singleColour);
            }
        }));
    }

    public void computeInterpolation(Config.Interoplation interoplation) {
        List<Integer> interpolated = ColourInterpolation.interpolateColours(staticColours, interoplation);
        animatedColours.clear();
        animatedColours.addAll(interpolated);
        computeColourArr();
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

    public void computeColourArr() {
        List<Integer> reversed = VLists.reverse(getColours());
        colourArr = new int[PALETTE_SIZE];
        for (int i = 0; i < PALETTE_SIZE; i++) {
            int colour = reversed.get(i);
            colourArr[i] = colour;
        }
    }

    public List<Integer> getColours() {
        return AllTheTrimsClient.getConfig().animate ? animatedColours : staticColours;
    }

    public int getAverageColour() {
        double[][] okLabSpace = new double[PALETTE_SIZE][3];
        for (int i = 0; i < colourArr.length; i++) {
            int colour = colourArr[i];
            double[] okLab = OkLabHelper.rgbToOKLab(colour);
            okLabSpace[i] = okLab;
        }
        double[] averaged = OkLabHelper.average(okLabSpace);
        return OkLabHelper.oklabToRGB(averaged);
    }

    public void cycleAnimatedColours() {
        Config config = AllTheTrimsClient.getConfig();
        if ((System.currentTimeMillis() - lastCycle) <= config.timeBetweenCycles / (config.animationInterpolation == Config.Interoplation.NONE ? 1 : 2)) return;

        int last = VLists.getLast(animatedColours);
        for (int i = animatedColours.size() - 1; i > 0; i--) {
            animatedColours.set(i, animatedColours.get(i - 1));
        }
        animatedColours.set(0, last);
        computeColourArr();
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
