package com.bawnorton.allthetrims.client.mixin.render;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import com.bawnorton.allthetrims.client.render.TrimRenderer;
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
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Map;

@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
    @Shadow @Final private SpriteAtlasTexture armorTrimsAtlas;

    public ArmorFeatureRendererMixin(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Inject(
            method = "renderArmor",
            at = @At(
                    value = "INVOKE",
                    //? if fabric {
                    /*target = "Lnet/minecraft/client/render/entity/feature/ArmorFeatureRenderer;renderTrim(Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/item/trim/ArmorTrim;Lnet/minecraft/client/render/entity/model/BipedEntityModel;Z)V"
                    *///?} elif neoforge {
                    target = "Lnet/minecraft/client/render/entity/feature/ArmorFeatureRenderer;renderTrim(Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/item/trim/ArmorTrim;Lnet/minecraft/client/model/Model;Z)V"
                    //?}
            )
    )
    private void captureContext(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model, CallbackInfo ci, @Local ArmorItem trimmed) {
        AllTheTrimsClient.getShaderManger().setContext(entity, trimmed);
    }

    @ModifyExpressionValue(
            //? if fabric {
            /*method = "renderTrim",
            *///?} elif neoforge {
            method = "renderTrim(Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/item/trim/ArmorTrim;Lnet/minecraft/client/model/Model;Z)V",
            //?}
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
    //? if fabric {
            /*method = "renderTrim",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"
            )
    )
    private void renderDynamicTrim(BipedEntityModel<T> instance, MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int uv, Operation<Void> original,
    *///?} elif neoforge {
            method = "renderTrim(Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/item/trim/ArmorTrim;Lnet/minecraft/client/model/Model;Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/Model;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"
            )
    )
    private void renderDynamicTrim(Model instance, MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int uv, Operation<Void> original,
    //?}
            @Local(argsOnly = true) ArmorTrim trim,
            @Local(argsOnly = true) VertexConsumerProvider vertexConsumers,
            @Local(argsOnly = true) RegistryEntry<ArmorMaterial> armourMaterial,
            @Local(argsOnly = true) boolean leggings,
            @Share("sprite") LocalRef<Sprite> spriteLocalRef) {
        Sprite sprite = spriteLocalRef.get();
        TrimRenderer trimRenderer = AllTheTrimsClient.getTrimRenderer();
        if (AllTheTrimsClient.getConfig().overrideExisting) {
            Identifier modelId = trimRenderer.getModelId(trim, armourMaterial, leggings);
            modelId = modelId.withPath(path -> {
                ArmorTrimMaterial trimMaterial = trim.getMaterial().value();
                Map<RegistryEntry<ArmorMaterial>, String> overrides = trimMaterial.overrideArmorMaterials();
                String assetId = overrides.getOrDefault(armourMaterial, trimMaterial.assetName());
                return path.replace(assetId, AllTheTrims.DYNAMIC);
            });
            trimRenderer.renderTrim(trim, sprite, matrixStack, vertexConsumers, light, uv, -1, modelId, armorTrimsAtlas, instance::render);
        } else {
            trimRenderer.renderTrim(trim, sprite, matrixStack, vertexConsumers, light, uv, -1, armorTrimsAtlas, instance::render);
        }
    }
}