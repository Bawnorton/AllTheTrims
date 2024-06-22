package com.bawnorton.allthetrims.client.mixin.render;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import com.bawnorton.allthetrims.client.api.DynamicTrimRenderer;
import com.bawnorton.allthetrims.client.colour.ColourHelper;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import java.util.Map;

@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity> {
    @Shadow @Final private SpriteAtlasTexture armorTrimsAtlas;

    @ModifyExpressionValue(
            //? if fabric {
            method = "renderTrim",
            //?} elif neoforge {
            /*method = "renderTrim(Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/item/trim/ArmorTrim;Lnet/minecraft/client/model/Model;Z)V",
            *///?}
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/texture/SpriteAtlasTexture;getSprite(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/texture/Sprite;"
            )
    )
    private Sprite captureSprite(Sprite original, @Share("sprite") LocalRef<Sprite> spriteLocalRef) {
        spriteLocalRef.set(original);
        return original;
    }

    //? if fabric {
    @WrapOperation(
            method = "renderTrim",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"
            )
    )
    private void renderDynamicTrim(BipedEntityModel<T> instance, MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int uv, Operation<Void> original,
            @Local(argsOnly = true) ArmorTrim trim,
            @Local(argsOnly = true) VertexConsumerProvider vertexConsumers,
            @Local(argsOnly = true) RegistryEntry<ArmorMaterial> armourMaterial,
            @Local(argsOnly = true) boolean leggings,
            @Share("sprite") LocalRef<Sprite> spriteLocalRef) {
        allthetrims$renderDynamicTrimInternal(instance, matrixStack, vertexConsumer, light, uv, original, trim, vertexConsumers, armourMaterial, leggings, spriteLocalRef.get());
    }
    //?}

    @Unique
    private void allthetrims$renderDynamicTrimInternal(BipedEntityModel<T> instance, MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int uv, Operation<Void> original, ArmorTrim trim, VertexConsumerProvider vertexConsumers, RegistryEntry<ArmorMaterial> armourMaterial, boolean leggings, Sprite sprite) {
        DynamicTrimRenderer trimRenderer = AllTheTrimsClient.getTrimRenderer();
        DynamicTrimRenderer.RenderCallback callback;
        if (trimRenderer.useLegacyRenderer(sprite)) {
            callback = Model::render;
        } else {
            callback = (model, matrices, vertices, l, overlay, colour) -> original.call(model, matrices, vertices, l, overlay);
        }
        if (AllTheTrimsClient.getConfig().overrideExisting) {
            Identifier modelId = trimRenderer.getModelId(trim, armourMaterial, leggings);
            modelId = modelId.withPath(path -> {
                ArmorTrimMaterial trimMaterial = trim.getMaterial().value();
                Map<RegistryEntry<ArmorMaterial>, String> overrides = trimMaterial.overrideArmorMaterials();
                String assetId = overrides.getOrDefault(armourMaterial, trimMaterial.assetName());
                return path.replace(assetId, AllTheTrims.DYNAMIC);
            });
            trimRenderer.renderTrim(trim, sprite, instance, matrixStack, vertexConsumer, vertexConsumers, light, uv, modelId, armorTrimsAtlas, callback);
        } else {
            trimRenderer.renderTrim(trim, sprite, instance, matrixStack, vertexConsumer, vertexConsumers, light, uv, armorTrimsAtlas, callback);
        }
    }
}