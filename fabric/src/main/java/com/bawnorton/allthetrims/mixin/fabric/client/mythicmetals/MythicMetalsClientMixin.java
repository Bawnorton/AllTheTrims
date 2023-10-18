package com.bawnorton.allthetrims.mixin.fabric.client.mythicmetals;

import com.bawnorton.allthetrims.annotation.ConditionalMixin;
import com.bawnorton.allthetrims.client.api.DynamicTrimRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import nourl.mythicmetals.MythicMetalsClient;
import nourl.mythicmetals.armor.HallowedArmor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(value = MythicMetalsClient.class, remap = false)
@ConditionalMixin(modid = "mythicmetals")
public abstract class MythicMetalsClientMixin {
    @Inject(method = "lambda$registerArmorRenderer$10", at = @At("HEAD"), cancellable = true)
    private static void lambda$registerArmorRenderer$10(EquipmentSlot slot, HallowedArmor armor, VertexConsumerProvider vertexConsumer, ItemStack stack, BipedEntityModel<?> model, MatrixStack matrices, int light, ArmorTrim trim, CallbackInfo ci) {
        boolean leggings = slot == EquipmentSlot.LEGS;
        ArmorMaterial material = armor.getMaterial();
        Sprite sprite = DynamicTrimRenderer.getAtlas()
            .getSprite(leggings ? trim.getLeggingsModelId(material) : trim.getGenericModelId(material));
        if (sprite.getContents().getId().equals(MissingSprite.getMissingSpriteId())) {
            DynamicTrimRenderer.renderTrim(material, matrices, vertexConsumer, light, trim, model, leggings);
            ci.cancel();
        }
    }
}
