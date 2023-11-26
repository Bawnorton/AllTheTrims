package com.bawnorton.allthetrims.fabric.mixin.client.mythicmetals;

import com.bawnorton.allthetrims.annotation.ConditionalMixin;
import com.bawnorton.allthetrims.fabric.extend.MythicMetalsClientExtender;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
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
@ConditionalMixin(modid = "mythicmetals", version = "<=0.18.2")
public abstract class MythicMetalsClient018Mixin implements MythicMetalsClientExtender {
    @Inject(method = "lambda$registerArmorRenderer$10", at = @At("HEAD"), cancellable = true, require = 0)
    private static void lambda$registerArmorRenderer$10(EquipmentSlot slot, HallowedArmor armor, VertexConsumerProvider vertexConsumer, ItemStack stack, BipedEntityModel<?> model, MatrixStack matrices, int light, ArmorTrim trim, CallbackInfo ci) {
        MythicMetalsClientExtender.handleArmorTrim(slot, armor, vertexConsumer, stack, model, matrices, light, trim, ci);
    }
}
