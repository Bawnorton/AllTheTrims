package com.bawnorton.allthetrims.mixin.client;

import com.bawnorton.allthetrims.client.api.DynamicTrimRenderer;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * See {@link com.bawnorton.allthetrims.mixin.fabric.client.ArmorFeatureRendererMixin} and {@link com.bawnorton.allthetrims.mixin.forge.client.ArmorFeatureRendererMixin} for the rest of the mixin<br><br>
 * For some reason the forge renderTrim method's signature was changed :forge_jank:
 */
@Mixin(value = ArmorFeatureRenderer.class, priority = 1500)
public abstract class ArmorFeatureRendererMixin {
    @ModifyExpressionValue(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/BakedModelManager;getAtlas(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/texture/SpriteAtlasTexture;"))
    private SpriteAtlasTexture captureAtlas(SpriteAtlasTexture atlas) {
        DynamicTrimRenderer.setAtlas(atlas);
        return atlas;
    }
}
