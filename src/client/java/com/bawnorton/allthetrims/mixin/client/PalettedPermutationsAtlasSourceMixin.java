package com.bawnorton.allthetrims.mixin.client;

import com.bawnorton.allthetrims.util.ImageUtil;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.atlas.PalettedPermutationsAtlasSource;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(PalettedPermutationsAtlasSource.class)
public abstract class PalettedPermutationsAtlasSourceMixin {
    @ModifyExpressionValue(method = "method_48486", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceManager;getResource(Lnet/minecraft/util/Identifier;)Ljava/util/Optional;"))
    private static Optional<Resource> onLoad(Optional<Resource> resource, ResourceManager resourceManager, Identifier id) {
        if(resource.isPresent() || !id.getPath().contains("trims/color_palettes/")) return resource;
        return Optional.of(new Resource(MinecraftClient.getInstance().getDefaultResourcePack(), () -> ImageUtil.toInputStream(ImageUtil.newBlankPalette())));
    }
}


