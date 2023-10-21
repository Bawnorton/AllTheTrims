package com.bawnorton.allthetrims.mixin.client.compat.immersivearmors;

import com.bawnorton.allthetrims.annotation.ConditionalMixin;
import com.bawnorton.allthetrims.client.api.DynamicTrimRenderer;
import immersive_armors.client.render.entity.piece.LayerPiece;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.trim.ArmorTrim;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(LayerPiece.class)
@ConditionalMixin(modid = "immersive_armors")
public abstract class LayerPieceMixin {
    @Shadow
    @Final
    protected SpriteAtlasTexture armorTrimsAtlas;

    @Inject(method = "renderTrim", at = @At("HEAD"), cancellable = true)
    private void renderDynamicTrim(ArmorMaterial material, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorTrim trim, BipedEntityModel<?> model, boolean leggings, CallbackInfo ci) {
        Sprite sprite = armorTrimsAtlas.getSprite(leggings ? trim.getLeggingsModelId(material) : trim.getGenericModelId(material));
        if (sprite.getContents().getId().equals(MissingSprite.getMissingSpriteId())) {
            DynamicTrimRenderer.renderTrim(material, matrices, vertexConsumers, light, trim, model, leggings);
            ci.cancel();
        }
    }
}
