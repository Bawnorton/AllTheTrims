package com.bawnorton.allthetrims.mixin.client;

import com.bawnorton.allthetrims.AllTheTrimsClient;
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
        if(!id.getPath().contains("trims/color_palettes") || resource.isPresent()) return resource;
        String path = id.getPath();
        Identifier itemId = new Identifier(path.substring("trims/color_palettes/".length()).replace("/", ":"));
        Resource newResource = new Resource(MinecraftClient.getInstance().getDefaultResourcePack(), () -> AllTheTrimsClient.getPaletteImageInputStream(itemId));
        return Optional.of(newResource);
    }
}
