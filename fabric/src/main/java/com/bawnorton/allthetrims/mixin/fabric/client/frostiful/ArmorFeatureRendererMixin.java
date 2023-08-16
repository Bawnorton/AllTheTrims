package com.bawnorton.allthetrims.mixin.fabric.client.frostiful;

import com.bawnorton.allthetrims.Compat;
import com.bawnorton.allthetrims.annotation.ConditionalMixin;
import com.bawnorton.allthetrims.client.api.DynamicTrimRenderer;
import com.bawnorton.allthetrims.client.util.PaletteHelper;
import com.github.thedeathlycow.frostiful.client.FTexturedRenderLayers;
import com.github.thedeathlycow.frostiful.tag.FTrimTags;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
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
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.List;

@Mixin(value = ArmorFeatureRenderer.class, priority = 500)
@ConditionalMixin(modid = "frostiful")
public abstract class ArmorFeatureRendererMixin {
    @Shadow @Final private SpriteAtlasTexture armorTrimsAtlas;

    @Unique
    private SpriteAtlasTexture allthetrims$frostiful$customArmorTrimsAtlas;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initFrostifulTrimAtlas(FeatureRendererContext<?, ?> context, BipedEntityModel<?> innerModel, BipedEntityModel<?> outerModel, BakedModelManager bakery, CallbackInfo ci) {
        this.allthetrims$frostiful$customArmorTrimsAtlas = bakery.getAtlas(FTexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE);
    }

    @Inject(method = "renderTrim", at = @At("HEAD"), cancellable = true)
    private void renderDynamicTrim(ArmorMaterial material, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorTrim trim, BipedEntityModel<?> model, boolean leggings, CallbackInfo ci) {
        if (trim.getPattern().isIn(FTrimTags.CUSTOM_PATTERNS)) {
            ArmorTrimMaterial trimMaterial = trim.getMaterial().value();
            String assetName =  trimMaterial.assetName();
            List<Color> palette = PaletteHelper.getPalette(trimMaterial.ingredient().value());
            Identifier modelId = leggings ? trim.getLeggingsModelId(material) : trim.getGenericModelId(material);
            for(int i = 0; i < 8; i++) {
                String layerPath = modelId.getPath().replace(assetName, i + "_" + assetName);
                Sprite sprite = allthetrims$frostiful$customArmorTrimsAtlas.getSprite(modelId.withPath(layerPath));
                VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(vertexConsumers.getBuffer(FTexturedRenderLayers.ARMOR_TRIMS_RENDER_LAYER));
                Color colour = palette.get(i);
                model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, colour.getRed() / 255f, colour.getGreen() / 255f, colour.getBlue() / 255f, Compat.getTrimTransparency());
            }
            ci.cancel();
            return;
        }

        Sprite sprite = armorTrimsAtlas.getSprite(leggings ? trim.getLeggingsModelId(material) : trim.getGenericModelId(material));
        if(sprite.getContents().getId().equals(MissingSprite.getMissingSpriteId())) {
            DynamicTrimRenderer.renderTrim(material, matrices, vertexConsumers, light, trim, model, leggings);
            ci.cancel();
        }
    }
}
