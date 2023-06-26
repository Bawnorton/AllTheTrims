package com.bawnorton.allthetrims.mixin.client.mythicmetals;

import com.bawnorton.allthetrims.api.DynamicTrimRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import nourl.mythicmetals.MythicMetalsClient;
import nourl.mythicmetals.armor.HallowedArmor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = MythicMetalsClient.class, remap = false)
public abstract class MythicMetalsClientMixin {
    /**
     * @author Bawnorton
     * @reason See {@link com.bawnorton.allthetrims.mixin.client.ArmorFeatureRendererMixin}
     */
    @Overwrite
    private static void lambda$registerArmorRenderer$10(EquipmentSlot slot, HallowedArmor armor, VertexConsumerProvider vertexConsumer, ItemStack stack, BipedEntityModel<LivingEntity> model, MatrixStack matrices, int light, ArmorTrim trim) {
        DynamicTrimRenderer.renderTrim(armor.getMaterial(), matrices, vertexConsumer, light, trim, model, slot == EquipmentSlot.LEGS);
    }
}
