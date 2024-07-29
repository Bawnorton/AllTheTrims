package com.bawnorton.allthetrims.mixin.compat.fabric.immersivearmors;

//? if fabric && <1.20.6 {

import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import com.bawnorton.allthetrims.client.colour.ARGBColourHelper;
import com.bawnorton.allthetrims.client.render.TrimRenderer;
import com.bawnorton.allthetrims.util.mixin.ConditionalMixin;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import immersive_armors.client.render.entity.piece.LayerPiece;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnusedMixin")
@Mixin(LayerPiece.class)
@ConditionalMixin("immersive_armors")
public abstract class LayerPieceMixin {
    @Shadow @Final protected SpriteAtlasTexture armorTrimsAtlas;

    @Inject(
            method = "render",
            at = @At("HEAD")
    )
    private <T extends LivingEntity, A extends BipedEntityModel<T>> void captureContext(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, ItemStack itemStack, float tickDelta, EquipmentSlot armorSlot, A armorModel, CallbackInfo ci) {
        AllTheTrimsClient.getTrimRenderer().setContext(entity, itemStack.getItem());
    }

    @ModifyExpressionValue(
            method = "renderTrim",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/texture/SpriteAtlasTexture;getSprite(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/texture/Sprite;"
            )
    )
    private Sprite captureSprite(Sprite original, @Share("sprite") LocalRef<Sprite> spriteLocalRef) {
        spriteLocalRef.set(original);
        return original;
    }

    @WrapOperation(
            method = "renderTrim",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"
            )
    )
    private <T extends LivingEntity> void renderDynamicTrim(BipedEntityModel<T> instance, MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int uv, float red, float green, float blue, float alpha, Operation<Void> original,
            @Local(argsOnly = true) ArmorTrim trim,
            @Local(argsOnly = true) VertexConsumerProvider vertexConsumers,
            @Local(argsOnly = true) ArmorMaterial armourMaterial,
            @Local(argsOnly = true) boolean leggings,
            @Share("sprite") LocalRef<Sprite> spriteLocalRef) {
        TrimRenderer.RenderCallback renderCallback = (matrices, vertices, light1, overlay, colour) -> {
            float r = ARGBColourHelper.floatFromChannel(ColorHelper.Argb.getRed(colour));
            float g = ARGBColourHelper.floatFromChannel(ColorHelper.Argb.getGreen(colour));
            float b = ARGBColourHelper.floatFromChannel(ColorHelper.Argb.getBlue(colour));
            float a = ARGBColourHelper.floatFromChannel(ColorHelper.Argb.getAlpha(colour));
            original.call(instance, matrices, vertices, light1, overlay, r, g, b, a);
        };
        TrimRenderer renderer = AllTheTrimsClient.getTrimRenderer();
        renderer.renderTrim(
                trim,
                armourMaterial,
                leggings,
                spriteLocalRef.get(),
                matrixStack,
                vertexConsumers,
                light,
                uv,
                -1,
                armorTrimsAtlas,
                renderCallback
        );
    }
}

//?}