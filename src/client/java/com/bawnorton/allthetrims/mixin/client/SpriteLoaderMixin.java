package com.bawnorton.allthetrims.mixin.client;

import com.bawnorton.allthetrims.AllTheTrimsClient;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteLoader;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpriteLoader.class)
public abstract class SpriteLoaderMixin {
    @Inject(method = "load(Lnet/minecraft/util/Identifier;Lnet/minecraft/resource/Resource;)Lnet/minecraft/client/texture/SpriteContents;", at = @At("HEAD"))
    private static void captureIdentifier(Identifier id, Resource resource, CallbackInfoReturnable<SpriteContents> cir, @Share("id") LocalRef<Identifier> idRef) {
        idRef.set(id);
    }

    @ModifyArg(method = "load(Lnet/minecraft/util/Identifier;Lnet/minecraft/resource/Resource;)Lnet/minecraft/client/texture/SpriteContents;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/SpriteContents;<init>(Lnet/minecraft/util/Identifier;Lnet/minecraft/client/texture/SpriteDimensions;Lnet/minecraft/client/texture/NativeImage;Lnet/minecraft/client/resource/metadata/AnimationResourceMetadata;)V"), index = 2)
    private static NativeImage captureImage(NativeImage image, @Share("id") LocalRef<Identifier> idRef) {
        AllTheTrimsClient.addPaletteImage(idRef.get(), image);
        return image;
    }
}
