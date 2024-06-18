package com.bawnorton.allthetrims.client.palette;

import com.bawnorton.allthetrims.client.mixin.accessor.SpriteContentsAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.NotNull;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class TrimPaletteGenerator {
    public TrimPalette generatePalette(Item item) {
        MinecraftClient client = MinecraftClient.getInstance();
        ItemRenderer itemRenderer = client.getItemRenderer();
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return TrimPalette.DEFAULT;
        }

        ItemStack stack = item.getDefaultStack();
        BakedModel itemModel = itemRenderer.getModel(stack, client.world, player, player.getId());

        List<BakedQuad> quads = new ArrayList<>();
        Random random = Random.create();
        for (Direction direction : Direction.values()) {
            random.setSeed(42);
            quads.addAll(itemModel.getQuads(null, direction, random));
        }
        random.setSeed(42);
        quads.addAll(itemModel.getQuads(null, null, random));

        List<Integer> colours = getColoursFromQuads(quads);
        List<Integer> vibrantPalette = generateVibrantPalette(colours);
        vibrantPalette = stretchPalette(vibrantPalette);
        vibrantPalette = sortPalette(vibrantPalette);

        return new TrimPalette(vibrantPalette);
    }

    /**
     * Attempt to generate the most vibrant palette from the item texture colours
     */
    private List<Integer> generateVibrantPalette(List<Integer> colours) {
        List<ColourHSB> hsbColours = toHSB(colours);

        hsbColours = hsbColours.stream()
                .distinct()
                .sorted(Comparator.comparing(ColourHSB::saturation)
                        .thenComparing(ColourHSB::brightness)
                        .reversed())
                .toList();

        List<Integer> vibrantPalette = new ArrayList<>();
        for (int i = 0; i < Math.min(TrimPalette.PALETTE_SIZE, hsbColours.size()); i++) {
            vibrantPalette.add(hsbColours.get(i).colour());
        }

        return vibrantPalette;
    }

    private List<Integer> sortPalette(List<Integer> colours) {
        List<ColourHSB> toSort = toHSB(colours);
        toSort.sort(Comparator.comparing(ColourHSB::brightness).reversed());
        return toSort.stream().map(ColourHSB::colour).toList();
    }

    private List<ColourHSB> toHSB(List<Integer> colours) {
        List<ColourHSB> hsbColours = new ArrayList<>();

        for (Integer colour : colours) {
            int red = colour >> 16 & 255;
            int green = colour >> 8 & 255;
            int blue = colour & 255;

            float[] hsbValues = Color.RGBtoHSB(red, green, blue, null);
            hsbColours.add(new ColourHSB(colour, hsbValues[1], hsbValues[2]));
        }
        return hsbColours;
    }

    /**
     * Generated palattes may be less than 8 pixels, so we need to stretch them
     */
    private List<Integer> stretchPalette(List<Integer> palette) {
        int size = palette.size();
        int targetSize = TrimPalette.PALETTE_SIZE;
        if (size >= targetSize) {
            return palette;
        }

        List<double[]> oklabPalette = new ArrayList<>();
        for (int rgb : palette) {
            double[] oklab = rgbToOKLab(rgb);
            oklabPalette.add(oklab);
        }

        List<double[]> stretchedOKLab = strechOkLab(targetSize, size, oklabPalette);

        List<Integer> stretchedPalette = new ArrayList<>(targetSize);
        for (double[] oklab : stretchedOKLab) {
            int rgb = oklabToRGB(oklab);
            stretchedPalette.add(rgb);
        }

        return stretchedPalette;
    }

    private @NotNull List<double[]> strechOkLab(int targetSize, int size, List<double[]> oklabPalette) {
        List<double[]> stretchedOKLab = new ArrayList<>(targetSize);
        for (int i = 0; i < targetSize; i++) {
            double t = (double) i / (targetSize - 1);
            int index1 = (int) Math.floor(t * (size - 1));
            int index2 = Math.min(index1 + 1, size - 1);
            double blend = (t * (size - 1)) - index1;

            double[] color1 = oklabPalette.get(index1);
            double[] color2 = oklabPalette.get(index2);
            double[] interpolatedColor = interpolateOKLab(color1, color2, blend);
            stretchedOKLab.add(interpolatedColor);
        }
        return stretchedOKLab;
    }

    private double[] interpolateOKLab(double[] color1, double[] color2, double blend) {
        double[] result = new double[3];
        for (int i = 0; i < 3; i++) {
            result[i] = color1[i] * (1 - blend) + color2[i] * blend;
        }
        return result;
    }

    // https://bottosson.github.io/posts/oklab/
    // magic numbers galore
    private double[] rgbToOKLab(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        double lR = gammaToLinear(r / 255D);
        double lG = gammaToLinear(g / 255D);
        double lB = gammaToLinear(b / 255D);

        double l = 0.4122214708 * lR + 0.5363325363 * lG + 0.0514459929 * lB;
        double m = 0.2119034982 * lR + 0.6806995451 * lG + 0.1073969566 * lB;
        double s = 0.0883024619 * lR + 0.2817188376 * lG + 0.6299787005 * lB;

        l = Math.cbrt(l);
        m = Math.cbrt(m);
        s = Math.cbrt(s);

        return new double[]{
                l * 0.2104542553 + m * 0.7936177850 + s * -0.0040720468,
                l * 1.9779984951 + m * -2.4285922050 + s * 0.4505937099,
                l * 0.0259040371 + m * 0.7827717662 + s * -0.8086757660
        };
    }

    private int oklabToRGB(double[] oklab) {
        double L = oklab[0];
        double A = oklab[1];
        double B = oklab[2];

        double l = L + A * 0.3963377774 + B * 0.2158037573;
        double m = L + A * -0.1055613458 + B * -0.0638541728;
        double s = L + A * -0.0894841775 + B * -1.2914855480;

        l = l * l * l;
        m = m * m * m;
        s = s * s * s;

        double r = l * 4.0767416621 + m * -3.3077115913 + s * 0.2309699292;
        double g = l * -1.2684380046 + m * 2.6097574011 + s * -0.3413193965;
        double b = l * -0.0041960863 + m * -0.7034186147 + s * 1.7076147010;

        r = 255 * linearToGamma(r);
        g = 255 * linearToGamma(g);
        b = 255 * linearToGamma(b);

        int iR = (int) Math.clamp(r, 0, 255);
        int iG = (int) Math.clamp(g, 0, 255);
        int iB = (int) Math.clamp(b, 0, 255);

        return iR << 16 | iG << 8 | iB;
    }

    private double gammaToLinear(double gamma) {
        if (gamma >= 0.04045) {
            return Math.pow((gamma + 0.055) / 1.055, 2.4);
        }
        return gamma / 12.92;
    }

    private double linearToGamma(double linear) {
        if (linear >= 0.0031308) {
            return 1.055 * Math.pow(linear, 1 / 2.4) - 0.055;
        }
        return linear * 12.92;
    }

    /**
     * Extracts every pixel colour in a quad's sprite ignoring transparent pixels
     */
    private @NotNull List<Integer> getColoursFromQuads(List<BakedQuad> quads) {
        List<Integer> colours = new ArrayList<>(quads.size() * 16 * 16);
        for (BakedQuad bakedQuad : quads) {
            int[] colourData = extractColours(bakedQuad.getSprite());
            for (int colour : colourData) {
                colours.add(colour);
            }
        }
        colours = colours.stream().filter(i -> i != 0).toList();
        return colours;
    }

    // [x * y] = rgb
    private int[] extractColours(Sprite sprite) {
        NativeImage spriteImage = ((SpriteContentsAccessor) sprite.getContents()).getImage();
        int width = spriteImage.getWidth();
        int height = spriteImage.getHeight();

        int[] colourData = new int[width * height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int colour = spriteImage.getColor(x, y);
                int alpha = ColorHelper.Abgr.getAlpha(colour);
                if (alpha == 0) {
                    continue;
                }

                int red = ColorHelper.Abgr.getRed(colour);
                int green = ColorHelper.Abgr.getGreen(colour);
                int blue = ColorHelper.Abgr.getBlue(colour);
                int packed = red << 16 | green << 8 | blue;
                colourData[x + y * width] = packed;
            }
        }

        return colourData;
    }

    private record ColourHSB(Integer colour, float saturation, float brightness) {
    }
}