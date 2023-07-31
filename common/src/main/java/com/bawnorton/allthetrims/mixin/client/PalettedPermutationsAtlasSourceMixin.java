package com.bawnorton.allthetrims.mixin.client;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.client.util.ImageUtil;
import com.bawnorton.allthetrims.client.util.PaletteHelper;
import com.bawnorton.allthetrims.util.DebugHelper;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.datafixers.util.Function3;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.atlas.PalettedPermutationsAtlasSource;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.*;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@Mixin(PalettedPermutationsAtlasSource.class)
public abstract class PalettedPermutationsAtlasSourceMixin {
    @ModifyArg(method = "method_48487", at = @At(value = "INVOKE", target = "Lcom/mojang/datafixers/Products$P3;apply(Lcom/mojang/datafixers/kinds/Applicative;Lcom/mojang/datafixers/util/Function3;)Lcom/mojang/datafixers/kinds/App;"), index = 1)
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
            newPermutations.put(AllTheTrims.TRIM_ASSET_NAME, new Identifier("trims/color_palettes/blank"));
            return function.apply(newTextures, paletteKey, newPermutations);
        };
    }

    @ModifyExpressionValue(method = "method_48486", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceManager;getResource(Lnet/minecraft/util/Identifier;)Ljava/util/Optional;"))
    private static Optional<Resource> addBlankPalette(Optional<Resource> optionalResource, ResourceManager resourceManager, Identifier identifier) {
        if (optionalResource.isPresent()) {
            String path = identifier.getPath();
            path = path.substring("trims_color_palettes_".length());
            PaletteHelper.putPalette(identifier.withPath(path), PaletteHelper.existingResourceToPalette(optionalResource.get()));
            return optionalResource;
        }
        return Optional.of(new Resource(MinecraftClient.getInstance().getDefaultResourcePack(), () -> ImageUtil.toInputStream(ImageUtil.newBlankPaletteImage())));
    }

    @Redirect(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceManager;getResource(Lnet/minecraft/util/Identifier;)Ljava/util/Optional;"))
    private Optional<Resource> getLayeredTrimResource(ResourceManager instance, Identifier identifier, @Share("identifier") LocalRef<Identifier> identifierRef) {
        identifierRef.set(identifier);
        Optional<Resource> original = instance.getResource(identifier);
        if (original.isPresent()) return original;

        String path = identifier.getPath();
        int pathEnd = path.lastIndexOf('_');
        Identifier originalIdentifier = pathEnd > 0
                ? identifier.withPath(path.substring(0, pathEnd) + ".png")
                : identifier;
        Optional<Resource> optionalResource = instance.getResource(originalIdentifier);
        if (optionalResource.isEmpty()) return optionalResource;

        int layer = Integer.parseInt(String.valueOf(path.charAt(path.length() - "x.png".length())));
        Resource resource = optionalResource.get();
        try (InputStream inputStream = resource.getInputStream()) {
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            if (bufferedImage == null) {
                throw new RuntimeException("Failed to read image from " + originalIdentifier);
            }
            Color colour = ImageUtil.getNthDarkestColour(bufferedImage, layer);
            BufferedImage newImage = ImageUtil.removeOtherColours(bufferedImage, colour);
            DebugHelper.saveLayeredTexture(newImage, identifier.toString());
            return Optional.of(new Resource(resource.getPack(), () -> ImageUtil.toInputStream(newImage)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @ModifyExpressionValue(method = "load", at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;"))
    private Set<Map.Entry<String, Supplier<IntUnaryOperator>>> removeAllNonBlankPalettes(Set<Map.Entry<String, Supplier<IntUnaryOperator>>> permutations, @Share("identifier") LocalRef<Identifier> identifierRef) {
        String path = identifierRef.get().getPath();
        String pattern = ".*_\\d.png";
        Set<Map.Entry<String, Supplier<IntUnaryOperator>>> newPermutations = new HashSet<>();
        if (!path.matches(pattern)) {
            for (Map.Entry<String, Supplier<IntUnaryOperator>> entry : permutations) {
                if (!entry.getKey().equals(AllTheTrims.TRIM_ASSET_NAME)) {
                    newPermutations.add(entry);
                }
            }
        } else {
            for (Map.Entry<String, Supplier<IntUnaryOperator>> entry : permutations) {
                if (entry.getKey().equals(AllTheTrims.TRIM_ASSET_NAME)) {
                    newPermutations.add(entry);
                }
            }
        }
        return newPermutations;
    }
}
