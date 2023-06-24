package com.bawnorton.allthetrims.mixin.client;

import com.bawnorton.allthetrims.util.ImageUtil;
import com.bawnorton.allthetrims.util.PaletteHelper;
import com.bawnorton.allthetrims.util.ResourceHelper;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.atlas.PalettedPermutationsAtlasSource;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.*;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@Mixin(PalettedPermutationsAtlasSource.class)
public abstract class PalettedPermutationsAtlasSourceMixin {
    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "<init>", at = @At("LOAD"), argsOnly = true)
    private List<Identifier> replaceWithLayeredTextures(List<Identifier> textures) {
        List<Identifier> newTextures = new ArrayList<>(textures.size() * 8);
        for(Identifier texture: textures) {
            for(int i = 0; i < 8; i++) {
                newTextures.add(texture.withSuffixedPath("_" + i));
            }
        }
        return newTextures;
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "<init>", at = @At("LOAD"), argsOnly = true)
    private Map<String, Identifier> replaceWithBlankPalette(Map<String, Identifier> original) {
        Map<String, Identifier> newMap = new HashMap<>(original);
        newMap.put("att-blank", new Identifier("trims/color_palettes/blank"));
        return newMap;
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

    @ModifyExpressionValue(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceManager;getResource(Lnet/minecraft/util/Identifier;)Ljava/util/Optional;"))
    private Optional<Resource> getLayeredTrimResource(Optional<Resource> original, ResourceManager resourceManager, @Local(name = "identifier2") Identifier identifier) {
        return ResourceHelper.getLayeredTrimResource(original, resourceManager, identifier);
    }
}
