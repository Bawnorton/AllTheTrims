package com.bawnorton.allthetrims.client.palette;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.client.colour.ColourHSB;
import com.bawnorton.allthetrims.client.colour.OkLabHelper;
import com.bawnorton.allthetrims.client.config.Config;
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
import java.util.ArrayList;
import java.util.Arrays;
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

        List<Integer> colours;
        if(itemModel.isBuiltin()) {
            colours = getColoursFromBuiltin(itemModel);
        } else {
            colours = getColoursFromStandard(itemModel);
        }

        List<Integer> vibrantPalette = generateVibrantPalette(colours);
        if(vibrantPalette.isEmpty()) {
            AllTheTrims.LOGGER.warn("Could not generate palette for {}", item.getName().getString());
            return TrimPalette.DEFAULT;
        }

        vibrantPalette = stretchPalette(vibrantPalette);
        vibrantPalette = sortPalette(vibrantPalette);

        return new TrimPalette(vibrantPalette);
    }

    /**
     * Attempt to generate the most vibrant palette from the item texture colours
     */
    private List<Integer> generateVibrantPalette(List<Integer> colours) {
        List<ColourHSB> hsbColours = ColourHSB.fromRGB(colours);

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
        List<ColourHSB> toSort = ColourHSB.fromRGB(colours);
        Config.PaletteSorting paletteSorting = AllTheTrims.getConfig().paletteSorting;
        Comparator<ColourHSB> comparator = Comparator.comparing(colourHSB -> {
            if(paletteSorting.isBrightness()) {
                return colourHSB.brightness();
            } else if (paletteSorting.isSaturation()) {
                return colourHSB.saturation();
            } else if (paletteSorting.isColour()) {
                return (float) colourHSB.colour();
            }
            return 0f;
        });
        if(paletteSorting.isReversed()) {
            comparator = comparator.reversed();
        }
        toSort.sort(comparator);
        return toSort.stream().map(ColourHSB::colour).toList();
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
            double[] oklab = OkLabHelper.rgbToOKLab(rgb);
            oklabPalette.add(oklab);
        }

        List<double[]> stretchedOKLab = OkLabHelper.strechOkLab(targetSize, size, oklabPalette);

        List<Integer> stretchedPalette = new ArrayList<>(targetSize);
        for (double[] oklab : stretchedOKLab) {
            int rgb = OkLabHelper.oklabToRGB(oklab);
            stretchedPalette.add(rgb);
        }

        return stretchedPalette;
    }

    private List<Integer> getColoursFromBuiltin(BakedModel model) {
        return Arrays.stream(extractColours(model.getParticleSprite())).boxed().toList();
    }

    private List<Integer> getColoursFromStandard(BakedModel model) {
        List<BakedQuad> quads = new ArrayList<>();
        Random random = Random.create();
        for (Direction direction : Direction.values()) {
            random.setSeed(42);
            quads.addAll(model.getQuads(null, direction, random));
        }
        random.setSeed(42);
        quads.addAll(model.getQuads(null, null, random));
        return getColoursFromQuads(quads);
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
}