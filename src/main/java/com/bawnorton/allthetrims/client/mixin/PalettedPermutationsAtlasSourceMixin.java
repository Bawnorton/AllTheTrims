package com.bawnorton.allthetrims.client.mixin;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.client.api.DynamicTrimRenderer;
import com.bawnorton.allthetrims.client.debug.Debugger;
import com.bawnorton.allthetrims.client.palette.TrimPalette;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.datafixers.util.Function3;
import it.unimi.dsi.fastutil.Pair;
import javax.imageio.ImageIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.atlas.PalettedPermutationsAtlasSource;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(PalettedPermutationsAtlasSource.class)
public abstract class PalettedPermutationsAtlasSourceMixin {
    @Unique
    private final Map<String, List<Integer>> allthetrims$trimTemplateColours = new HashMap<>();
    @Unique
    private final List<Identifier> allthetrims$skippedLayers = new ArrayList<>();

    @ModifyArg(
            method = "method_48487",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/datafixers/Products$P3;apply(Lcom/mojang/datafixers/kinds/Applicative;Lcom/mojang/datafixers/util/Function3;)Lcom/mojang/datafixers/kinds/App;",
                    remap = false
            ),
            index = 1
    )
    private static <R> Function3<List<Identifier>, Identifier, Map<String, Identifier>, R> createDynamicTrimPermutation(Function3<List<Identifier>, Identifier, Map<String, Identifier>, R> function) {
        return (textures, paletteKey, palettedPermutations) -> {
            if (!paletteKey.getPath().contains("trim_palette")) return function.apply(textures, paletteKey, palettedPermutations);

            List<Identifier> newTextures = new ArrayList<>(textures.size() * 9);
            for (Identifier texture : textures) {
                newTextures.add(texture);
                for (int i = 0; i < 8; i++) {
                    newTextures.add(texture.withSuffixedPath("_" + i));
                }
            }

            Map<String, Identifier> newPermutations = new HashMap<>(palettedPermutations);
            newPermutations.put("dynamic", Identifier.ofVanilla("trims/color_palettes/dynamic"));
            return function.apply(newTextures, paletteKey, newPermutations);
        };
    }

    @WrapOperation(
            method = "open",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resource/ResourceManager;getResource(Lnet/minecraft/util/Identifier;)Ljava/util/Optional;"
            )
    )
    private static Optional<Resource> addDynamicPaletteImage(ResourceManager instance, Identifier identifier, Operation<Optional<Resource>> original) {
        Optional<Resource> existing = original.call(instance, identifier);
        if (existing.isPresent()) return existing;
        if (!identifier.equals(Identifier.ofVanilla("textures/trims/color_palettes/dynamic.png"))) return existing;

        ResourcePack defaultPack = MinecraftClient.getInstance().getDefaultResourcePack();
        Resource dynamicResource = allthetrims$createGradientTrimPaletteResource(defaultPack);
        return Optional.of(dynamicResource);
    }

    @Unique
    private static @NotNull Resource allthetrims$createGradientTrimPaletteResource(ResourcePack defaultPack) {
        float dif = 255 / 8f;
        List<Integer> colours = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            int brightness = (int) (255 - dif * i);
            colours.add(ColorHelper.Argb.getArgb(brightness, brightness, brightness));
        }
        TrimPalette palette = new TrimPalette(colours);
        return new Resource(defaultPack, palette::toInputStream);
    }

    @WrapOperation(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceManager;getResource(Lnet/minecraft/util/Identifier;)Ljava/util/Optional;"))
    private Optional<Resource> getLayeredTrimResource(ResourceManager instance, Identifier identifier, Operation<Optional<Resource>> original, @Share("identifier") LocalRef<Identifier> identifierRef) {
        identifierRef.set(identifier);
        Optional<Resource> originalResource = original.call(instance, identifier);
        if (originalResource.isPresent()) return originalResource;

        String path = identifier.getPath();
        int pathEnd = path.lastIndexOf('_');
        Identifier originalIdentifier = pathEnd > 0 ? identifier.withPath(path.substring(0, pathEnd) + ".png") : identifier;
        Optional<Resource> optionalResource = instance.getResource(originalIdentifier);
        if (optionalResource.isEmpty()) return optionalResource;

        Pattern pattern = Pattern.compile("\\d+(?![\\d\\D]*\\d)");
        Matcher matcher = pattern.matcher(path);
        if (!matcher.find()) {
            return optionalResource;
        }

        int layer = Integer.parseInt(matcher.group());
        Resource resource = optionalResource.get();
        try (InputStream inputStream = resource.getInputStream()) {
            BufferedImage bufferedImage = ImageIO.read(inputStream);

            int layerColour = allthetrims$getLayerColour(bufferedImage, originalIdentifier.getPath(), layer);
            Pair<BufferedImage, Boolean> newImage;
            if(originalIdentifier.getPath().startsWith("textures/trims/items")) {
                newImage = allthetrims$maskToColour(bufferedImage, layerColour, layerColour);
            } else {
                newImage = allthetrims$maskToColour(bufferedImage, layerColour, 0xFFFFFFFF);
            }

            if(newImage.right()) {
                allthetrims$skippedLayers.add(identifier);
            } else {
                DynamicTrimRenderer.setMaxSupportedLayer(originalIdentifier, layer);
            }

            Debugger.createImage("%s".formatted(originalIdentifier.getPath()), bufferedImage);
            Debugger.createImage("%s".formatted(identifier.getPath()), newImage.left());

            return Optional.of(new Resource(resource.getPack(), () -> allthetrims$asInputStream(newImage.left())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Unique
    private int allthetrims$getLayerColour(BufferedImage bufferedImage, String trimId, int layer) {
        List<Integer> layerColours = allthetrims$trimTemplateColours.computeIfAbsent(trimId, id -> {
            int[] colours = bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null, 0, bufferedImage.getWidth());
            Set<Integer> uniqueColours = new HashSet<>();
            for (int colour : colours) {
                uniqueColours.add(colour);
            }
            uniqueColours.remove(0);
            return uniqueColours.stream()
                            .sorted(Comparator.comparingInt(i -> i))
                            .toList();
        });
        if (layer >= layerColours.size()) return -1;
        return layerColours.get(layer);
    }

    @Unique
    private Pair<BufferedImage, Boolean> allthetrims$maskToColour(BufferedImage bufferedImage, int mask, int toColour) {
        BufferedImage maskedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        if(mask == -1) return Pair.of(maskedImage, true);

        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                int colour = bufferedImage.getRGB(x, y);
                int alpha = colour >> 24 & 255;
                if (alpha == 0) continue;

                if (colour == mask) {
                    maskedImage.setRGB(x, y, toColour);
                }
            }
        }
        return Pair.of(maskedImage, false);
    }

    @Unique
    private InputStream allthetrims$asInputStream(BufferedImage bufferedImage) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", baos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }

    @ModifyExpressionValue(method = "load", at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;"))
    private Set<Map.Entry<String, Supplier<IntUnaryOperator>>> removeAllNonBlankPalettes(Set<Map.Entry<String, Supplier<IntUnaryOperator>>> permutations, @Share("identifier") LocalRef<Identifier> identifierRef) {
        Identifier identifier = identifierRef.get();
        if(allthetrims$skippedLayers.contains(identifier)) return Collections.emptySet();

        String path = identifier.getPath();
        String pattern = ".*_\\d.png";
        Set<Map.Entry<String, Supplier<IntUnaryOperator>>> newPermutations = new HashSet<>();
        if (!path.matches(pattern)) {
            newPermutations.addAll(permutations);
        } else {
            for (Map.Entry<String, Supplier<IntUnaryOperator>> entry : permutations) {
                if (entry.getKey().equals("dynamic")) {
                    newPermutations.add(entry);
                }
            }
        }
        if(AllTheTrims.getConfig().overrideExisting) {
            newPermutations.removeIf(entry -> !entry.getKey().equals("dynamic"));
        }
        return newPermutations;
    }
}
