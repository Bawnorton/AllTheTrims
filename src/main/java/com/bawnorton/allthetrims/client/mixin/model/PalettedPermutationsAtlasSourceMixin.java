package com.bawnorton.allthetrims.client.mixin.model;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import com.bawnorton.allthetrims.client.palette.TrimPalette;
import com.bawnorton.allthetrims.versioned.VIdentifier;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.datafixers.util.Function3;
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
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(PalettedPermutationsAtlasSource.class)
public abstract class PalettedPermutationsAtlasSourceMixin {
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
            newPermutations.put(AllTheTrims.DYNAMIC, VIdentifier.ofVanilla("trims/color_palettes/%s".formatted(AllTheTrims.DYNAMIC)));
            return function.apply(newTextures, paletteKey, newPermutations);
        };
    }

    @WrapOperation(
            //? if >1.20.6 {
            /*method = "open",
            *///?} else {
            method = "method_48486",
            //?}
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resource/ResourceManager;getResource(Lnet/minecraft/util/Identifier;)Ljava/util/Optional;"
            )
    )
    private static Optional<Resource> addDynamicPaletteImage(ResourceManager instance, Identifier identifier, Operation<Optional<Resource>> original) {
        Optional<Resource> existing = original.call(instance, identifier);
        if (existing.isPresent()) return existing;
        if (!identifier.equals(VIdentifier.ofVanilla("textures/trims/color_palettes/%s.png".formatted(AllTheTrims.DYNAMIC)))) return existing;

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
            colours.add(ColorHelper.Argb.getArgb(255, brightness, brightness, brightness));
        }
        TrimPalette palette = new TrimPalette(colours);
        return new Resource(defaultPack, palette::toInputStream);
    }

    @WrapOperation(
            method = "load",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resource/ResourceManager;getResource(Lnet/minecraft/util/Identifier;)Ljava/util/Optional;"
            )
    )
    private Optional<Resource> getLayeredTrimResource(ResourceManager instance, Identifier layerId, Operation<Optional<Resource>> original, @Share("layerId") LocalRef<Identifier> layerIdRef) {
        layerIdRef.set(layerId);
        Optional<Resource> originalResource = original.call(instance, layerId);
        if (originalResource.isPresent()) return originalResource;

        String path = layerId.getPath();
        int pathEnd = path.lastIndexOf('_');
        Identifier originalLayerId = pathEnd > 0 ? layerId.withPath(path.substring(0, pathEnd) + ".png") : layerId;
        Optional<Resource> optionalResource = instance.getResource(originalLayerId);
        if (optionalResource.isEmpty()) return optionalResource;

        Pattern pattern = Pattern.compile("\\d+(?![\\d\\D]*\\d)");
        Matcher matcher = pattern.matcher(path);
        if (!matcher.find()) {
            return optionalResource;
        }

        int layer = Integer.parseInt(matcher.group());
        Resource resource = optionalResource.get();
        try (InputStream inputStream = resource.getInputStream()) {
            BufferedImage layerImage = ImageIO.read(inputStream);

            return AllTheTrimsClient.getArmourModelLoader().loadLayeredResource(layerId, layerImage, originalLayerId, layer, resource.getPack());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @ModifyExpressionValue(
            method = "load",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;entrySet()Ljava/util/Set;"
            )
    )
    private Set<Map.Entry<String, Supplier<IntUnaryOperator>>> removeAllNonBlankPalettes(Set<Map.Entry<String, Supplier<IntUnaryOperator>>> permutations, @Share("layerId") LocalRef<Identifier> layerIdRef) {
        return AllTheTrimsClient.getArmourModelLoader().cleanPermutations(permutations, layerIdRef.get());
    }
}
