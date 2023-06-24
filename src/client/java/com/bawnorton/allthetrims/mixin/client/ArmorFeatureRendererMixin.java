package com.bawnorton.allthetrims.mixin.client;

import com.bawnorton.allthetrims.util.PaletteHelper;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.awt.*;
import java.util.List;

@Mixin(value = ArmorFeatureRenderer.class, priority = 1500)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> {
    @Shadow @Final private SpriteAtlasTexture armorTrimsAtlas;

    /**
     * @author Bawnorton
     * @reason Completely replace the renderTrim method to render all the dynamic layers
     */
    @Overwrite
    private void renderTrim(ArmorMaterial material, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorTrim trim, A model, boolean leggings) {
        Identifier modelId = leggings ? trim.getLeggingsModelId(material) : trim.getGenericModelId(material);
        String path = modelId.getPath();
        ArmorTrimMaterial trimMaterial = trim.getMaterial().value();
        Item trimItem = trimMaterial.ingredient().value();
        String assetName =  trimMaterial.assetName();
        Identifier trimAssetId = new Identifier(Registries.ITEM.getId(trimItem).getNamespace(), assetName);
        List<Color> palette;
        if(PaletteHelper.paletteExists(trimAssetId)) {
            palette = PaletteHelper.WHITE_PALETTE;
        } else {
            palette = PaletteHelper.getPalette(trimItem);
        }
        for(int i = 0; i < 8; i++) {
            String layerPath = path.replace(assetName, i + "_" + assetName);
            Sprite sprite = armorTrimsAtlas.getSprite(modelId.withPath(layerPath));
            VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(vertexConsumers.getBuffer(TexturedRenderLayers.getArmorTrims()));
            Color colour = palette.get(i);
            model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, colour.getRed() / 255f, colour.getGreen() / 255f, colour.getBlue() / 255f, 1.0F);
        }
    }
}
