package com.bawnorton.allthetrims.mixin.client;

import com.bawnorton.allthetrims.client.api.DynamicTrimRenderer;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.trim.ArmorTrim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ArmorFeatureRenderer.class, priority = 1500)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> {
    @ModifyExpressionValue(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/BakedModelManager;getAtlas(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/texture/SpriteAtlasTexture;"))
    private SpriteAtlasTexture captureAtlas(SpriteAtlasTexture atlas) {
        DynamicTrimRenderer.setAtlas(atlas);
        return atlas;
    }

    /**
     * @author Bawnorton
     * @reason Completely replace the renderTrim method to render all the dynamic layers
     */
    @Overwrite
    private void renderTrim(ArmorMaterial material, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorTrim trim, A model, boolean leggings) {
        DynamicTrimRenderer.renderTrim(material, matrices, vertexConsumers, light, trim, model, leggings);
    }
}
