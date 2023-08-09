package com.bawnorton.allthetrims.mixin.forge.client;

import com.bawnorton.allthetrims.client.api.DynamicTrimRenderer;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.trim.ArmorTrim;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ArmorFeatureRenderer.class, priority = 1500)
public abstract class ArmorFeatureRendererMixin {
    /**
     * Forge uses a <a href="https://github.com/MinecraftForge/MinecraftForge/blob/ccbc697bad9b689fbb44cb1de090a0650c8925bc/patches/minecraft/net/minecraft/client/renderer/entity/layers/HumanoidArmorLayer.java.patch#L48-L55">hacky bridge</a> that changes the signature of the method
     */
    @Shadow
    @Final
    private SpriteAtlasTexture armorTrimsAtlas;

    @Inject(method = "renderTrim(Lnet/minecraft/item/ArmorMaterial;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/item/trim/ArmorTrim;Lnet/minecraft/client/model/Model;Z)V", at = @At("HEAD"), cancellable = true)
    private void renderDynamicTrim(ArmorMaterial material, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorTrim trim, Model model, boolean leggings, CallbackInfo ci) {
        Sprite sprite = armorTrimsAtlas.getSprite(leggings ? trim.getLeggingsModelId(material) : trim.getGenericModelId(material));
        if(sprite.getContents().getId().equals(MissingSprite.getMissingSpriteId())) {
            DynamicTrimRenderer.renderTrim(material, matrices, vertexConsumers, light, trim, (BipedEntityModel<?>) model, leggings);
            ci.cancel();
        }
    }
}
