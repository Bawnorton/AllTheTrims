package com.bawnorton.allthetrims.mixin.fabric.client.betterend;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import org.betterx.bclib.client.render.HumanoidArmorRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(HumanoidArmorRenderer.class)
public abstract class HumanoidArmorRendererMixin {

    @Unique
    private SpriteAtlasTexture armorTrimsAtlas;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lorg/betterx/bclib/client/render/HumanoidArmorRenderer;renderModel(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/model/BipedEntityModel;Lnet/minecraft/util/Identifier;FFF)V", shift = At.Shift.AFTER))
    private void renderTrims(MatrixStack matrices, VertexConsumerProvider buffer, ItemStack stack, LivingEntity entity, EquipmentSlot slot, int light, BipedEntityModel<LivingEntity> parentModel, CallbackInfo ci) {
        if(armorTrimsAtlas == null) armorTrimsAtlas = MinecraftClient.getInstance().getBakedModelManager().getAtlas(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE);

        ArmorItem armourItem = (ArmorItem) stack.getItem();
        ArmorMaterial material = armourItem.getMaterial();
        ArmorTrim.getTrim(entity.getWorld().getRegistryManager(), stack).ifPresent((trim) -> {
            this.renderTrim(material, entity, slot, matrices, buffer, light, trim, parentModel);
        });
    }

    private void renderTrim(ArmorMaterial material, LivingEntity entity, EquipmentSlot slot, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorTrim trim, BipedEntityModel<LivingEntity> model) {
        // TODO: Implement
        // can't use vanilla trim rendering system as the model is a different size
    }
}
