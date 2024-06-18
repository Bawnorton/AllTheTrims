package com.bawnorton.allthetrims.client.mixin;

import com.bawnorton.allthetrims.client.palette.TrimPalette;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.util.Function3;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

            Map<String, Identifier> newPermutations = new HashMap<>(palettedPermutations);
            newPermutations.put("dynamic", Identifier.ofVanilla("trims/color_palettes/dynamic"));
            return function.apply(textures, paletteKey, newPermutations);
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
        TrimPalette palette = new TrimPalette(List.of(
                ColorHelper.Argb.getArgb(224, 224, 224),
                ColorHelper.Argb.getArgb(192, 192, 192),
                ColorHelper.Argb.getArgb(160, 160, 160),
                ColorHelper.Argb.getArgb(128, 128, 128),
                ColorHelper.Argb.getArgb(96, 96, 96),
                ColorHelper.Argb.getArgb(64, 64, 64),
                ColorHelper.Argb.getArgb(32, 32, 32),
                ColorHelper.Argb.getArgb(0, 0, 0)
        ));
        return new Resource(defaultPack, palette::toInputStream);
    }
}
