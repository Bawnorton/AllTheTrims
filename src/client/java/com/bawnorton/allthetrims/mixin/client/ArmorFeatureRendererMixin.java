package com.bawnorton.allthetrims.mixin.client;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.util.ImageUtil;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.trim.ArmorTrim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.awt.*;

@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin {
    @Inject(method = "renderTrim", at = @At("HEAD"))
    private <T extends LivingEntity, A extends BipedEntityModel<T>> void captureTrim(ArmorMaterial material, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorTrim trim, A model, boolean leggings, CallbackInfo ci, @Share("trim") LocalRef<ArmorTrim> trimLocalRef) {
        trimLocalRef.set(trim);
    }

    @ModifyArgs(method = "renderTrim", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    private void setArmorTrimColour(Args args, @Share("trim") LocalRef<ArmorTrim> trimLocalRef) {
        ArmorTrim trim = trimLocalRef.get();
        Item material = trim.getMaterial().value().ingredient().value();
        if(AllTheTrims.isUsedAsMaterial(material)) return;
        Color colour = ImageUtil.getAverageColour(material);
        args.set(4, colour.getRed() / 255f);
        args.set(5, colour.getGreen() / 255f);
        args.set(6, colour.getBlue() / 255f);
    }
}
