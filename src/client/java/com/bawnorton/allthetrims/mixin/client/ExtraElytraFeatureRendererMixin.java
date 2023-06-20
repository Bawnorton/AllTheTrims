package com.bawnorton.allthetrims.mixin.client;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.util.ImageUtil;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.kikugie.elytratrims.render.ExtraElytraFeatureRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.awt.*;

@Pseudo
@Mixin(ExtraElytraFeatureRenderer.class)
public abstract class ExtraElytraFeatureRendererMixin {
    @ModifyExpressionValue(method = "renderElytraTrims", at = @At(value = "INVOKE", target = "Ljava/util/Optional;orElse(Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object captureTrim(Object trim, @Share("trim") LocalRef<Item> trimLocalRef) {
        trimLocalRef.set(((ArmorTrim) trim).getMaterial().value().ingredient().value());
        return trim;
    }

    @Inject(method = "renderElytraTrims", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/Sprite;getTextureSpecificVertexConsumer(Lnet/minecraft/client/render/VertexConsumer;)Lnet/minecraft/client/render/VertexConsumer;"), cancellable = true)
    private void checkWhitelist(MatrixStack matrices, VertexConsumerProvider provider, LivingEntity entity, ItemStack stack, int light, float alpha, CallbackInfo ci, @Share("trim") LocalRef<Item> trimLocalRef) {
        if(!AllTheTrims.isUsedAsMaterial(trimLocalRef.get()) && AllTheTrims.notWhitelisted(trimLocalRef.get())) ci.cancel();
    }

    @ModifyArgs(method = "renderElytraTrims", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/ElytraEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    private void renderATTtrim(Args args, @Share("trim") LocalRef<Item> trimLocalRef) {
        Item material = trimLocalRef.get();
        if(material == null) return;
        if(AllTheTrims.isUsedAsMaterial(material)) return;

        Color colour = ImageUtil.getAverageColour(material);
        args.set(4, colour.getRed() / 255f);
        args.set(5, colour.getGreen() / 255f);
        args.set(6, colour.getBlue() / 255f);
    }
}
