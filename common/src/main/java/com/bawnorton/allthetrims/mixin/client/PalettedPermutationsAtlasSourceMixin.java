package com.bawnorton.allthetrims.mixin.client;

import com.bawnorton.allthetrims.client.util.ImageUtil;
import com.bawnorton.allthetrims.client.util.PaletteHelper;
import com.bawnorton.allthetrims.util.DebugHelper;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.atlas.PalettedPermutationsAtlasSource;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
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

    @Redirect(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceManager;getResource(Lnet/minecraft/util/Identifier;)Ljava/util/Optional;"))
    private Optional<Resource> getLayeredTrimResource(ResourceManager instance, Identifier identifier) {
        Optional<Resource> original = instance.getResource(identifier);
        if(original.isPresent()) return original;

        String path = identifier.getPath();
        Identifier originalIdentifier = identifier.withPath(path.substring(0, path.lastIndexOf('_')) + ".png");
        Optional<Resource> optionalResource = instance.getResource(originalIdentifier);
        if(optionalResource.isEmpty()) return optionalResource;

        int layer = Integer.parseInt(String.valueOf(path.charAt(path.length() - "x.png".length())));
        Resource resource = optionalResource.get();
        try(InputStream inputStream = resource.getInputStream()) {
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            if(bufferedImage == null) {
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
}
