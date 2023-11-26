package com.bawnorton.allthetrims.fabric.mixin.client.frostiful;

import com.bawnorton.allthetrims.annotation.ConditionalMixin;
import com.bawnorton.allthetrims.client.api.DynamicTrimRenderer;
import com.bawnorton.mixinsquared.TargetHandler;
import com.github.thedeathlycow.frostiful.client.FTexturedRenderLayers;
import com.github.thedeathlycow.frostiful.tag.FTrimTags;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.trim.ArmorTrim;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(value = ArmorFeatureRenderer.class, priority = 1500)
@ConditionalMixin(modid = "frostiful")
public abstract class ArmorFeatureRendererMixin {
    @Shadow
    @Final
    private SpriteAtlasTexture armorTrimsAtlas;

    @Unique
    private SpriteAtlasTexture allthetrims$frostiful$customArmorTrimsAtlas;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initFrostifulTrimAtlas(FeatureRendererContext<?, ?> context, BipedEntityModel<?> innerModel, BipedEntityModel<?> outerModel, BakedModelManager bakery, CallbackInfo ci) {
        this.allthetrims$frostiful$customArmorTrimsAtlas = bakery.getAtlas(FTexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE);
    }

    @TargetHandler(mixin = "com.github.thedeathlycow.frostiful.mixins.client.ArmorFeatureRendererMixin", name = "renderCustomTrim")
    @Inject(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "net/minecraft/client/texture/Sprite.getTextureSpecificVertexConsumer (Lnet/minecraft/client/render/VertexConsumer;)Lnet/minecraft/client/render/VertexConsumer;"), cancellable = true)
    private void renderFrostifulDynamicTrim(ArmorMaterial material, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorTrim trim, BipedEntityModel<?> model, boolean leggings, CallbackInfo original, CallbackInfo ci) {
        Sprite sprite = allthetrims$frostiful$customArmorTrimsAtlas.getSprite(leggings ? trim.getLeggingsModelId(material) : trim.getGenericModelId(material));
        if (sprite.getContents().getId().equals(MissingSprite.getMissingSpriteId())) {
            DynamicTrimRenderer.renderTrim(material, matrices, vertexConsumers, light, trim, model, leggings, allthetrims$frostiful$customArmorTrimsAtlas, FTexturedRenderLayers.ARMOR_TRIMS_RENDER_LAYER);
            original.cancel();
            ci.cancel();
        }
    }

    @TargetHandler(mixin = "com.github.thedeathlycow.frostiful.mixins.client.ArmorFeatureRendererMixin", name = "renderCustomTrim")
    @Inject(method = "@MixinSquared:Handler", at = @At("HEAD"), cancellable = true)
    private void renderDynamicTrim(ArmorMaterial material, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorTrim trim, BipedEntityModel<?> model, boolean leggings, CallbackInfo original, CallbackInfo ci) {
        if (!trim.getPattern().isIn(FTrimTags.CUSTOM_PATTERNS)) {
            Sprite sprite = armorTrimsAtlas.getSprite(leggings ? trim.getLeggingsModelId(material) : trim.getGenericModelId(material));
            if (sprite.getContents().getId().equals(MissingSprite.getMissingSpriteId())) {
                DynamicTrimRenderer.renderTrim(material, matrices, vertexConsumers, light, trim, model, leggings);
                original.cancel();
                ci.cancel();
            }
        }
    }
}
