package com.bawnorton.allthetrims.client.api;

import com.bawnorton.allthetrims.Compat;
import com.bawnorton.allthetrims.client.util.PaletteHelper;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.List;

public abstract class DynamicTrimRenderer {
    private static SpriteAtlasTexture armorTrimsAtlas;

    public static SpriteAtlasTexture getAtlas() {
        return armorTrimsAtlas;
    }

    public static void setAtlas(SpriteAtlasTexture atlas) {
        armorTrimsAtlas = atlas;
    }

    public static void renderTrim(ArmorMaterial material, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorTrim trim, BipedEntityModel<?> model, boolean leggings) {
        renderTrim(material, matrices, vertexConsumers, light, trim, model, leggings, Compat.getTrimRenderLayer());
    }

    public static void renderTrim(ArmorMaterial material, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorTrim trim, BipedEntityModel<?> model, boolean leggings, RenderLayer layer) {
        renderTrim(material, matrices, vertexConsumers, light, trim, model, leggings, armorTrimsAtlas, layer);
    }

    public static void renderTrim(ArmorMaterial material, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorTrim trim, BipedEntityModel<?> model, boolean leggings, SpriteAtlasTexture atlas, RenderLayer layer) {
        ArmorTrimMaterial trimMaterial = trim.getMaterial().value();
        String assetName = trimMaterial.assetName();
        List<Color> palette = PaletteHelper.getPalette(trimMaterial.ingredient().value());
        Identifier modelId = leggings ? trim.getLeggingsModelId(material) : trim.getGenericModelId(material);
        for (int i = 0; i < 8; i++) {
            String layerPath = modelId.getPath().replace(assetName, i + "_" + assetName);
            Sprite sprite = atlas.getSprite(modelId.withPath(layerPath));
            VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(vertexConsumers.getBuffer(layer));
            Color colour = palette.get(i);
            model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, colour.getRed() / 255f, colour.getGreen() / 255f, colour.getBlue() / 255f, Compat.getTrimTransparency());
        }
    }
}
