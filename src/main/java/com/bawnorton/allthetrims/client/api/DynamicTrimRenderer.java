package com.bawnorton.allthetrims.client.api;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.client.compat.Compat;
import com.bawnorton.allthetrims.client.palette.TrimPalette;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DynamicTrimRenderer {
    private static final Map<Identifier, Integer> maxSupportedLayers = new HashMap<>();
    private static SpriteAtlasTexture armorTrimsAtlas;

    public static SpriteAtlasTexture getAtlas() {
        return armorTrimsAtlas;
    }

    public static void setAtlas(SpriteAtlasTexture atlas) {
        armorTrimsAtlas = atlas;
    }

    public static void setMaxSupportedLayer(Identifier trimPattern, int layer) {
        int existingLayer = maxSupportedLayers.getOrDefault(trimPattern, -1);
        if (layer > existingLayer) {
            maxSupportedLayers.put(trimPattern, layer);
        }
    }

    public static void renderTrim(RegistryEntry<ArmorMaterial> armourMaterial, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorTrim trim, BipedEntityModel<?> model, boolean leggings) {
        renderTrim(armourMaterial, matrices, vertexConsumers, light, trim, model, leggings, Compat.getTrimRenderLayer(trim.getPattern().value().decal()));
    }

    public static void renderTrim(RegistryEntry<ArmorMaterial> armourMaterial, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorTrim trim, BipedEntityModel<?> model, boolean leggings, RenderLayer layer) {
        renderTrim(armourMaterial, matrices, vertexConsumers, light, trim, model, leggings, armorTrimsAtlas, layer);
    }

    public static void renderTrim(RegistryEntry<ArmorMaterial> armourMaterial, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorTrim trim, BipedEntityModel<?> model, boolean leggings, SpriteAtlasTexture atlas, RenderLayer layer) {
        ArmorTrimMaterial trimMaterial = trim.getMaterial().value();
        Item trimItem = trimMaterial.ingredient().value();
        String assetName = trimMaterial.assetName();

        Identifier modelId = leggings ? trim.getLeggingsModelId(armourMaterial) : trim.getGenericModelId(armourMaterial);
        if(AllTheTrims.getConfig().overrideExisting) {
            modelId = modelId.withPath(path -> "%s_dynamic".formatted(path.substring(0, path.length() - assetName.length() - 1)));
        }
        String modelPath = modelId.getPath();
        Identifier patternId = modelId.withPath("textures/%s.png".formatted(modelPath.substring(0, modelPath.lastIndexOf("_"))));
        int maxSupportedLayer = maxSupportedLayers.getOrDefault(patternId, -1) + 1;

        TrimPalette trimPalette = AllTheTrims.getTrimPalettes().getTrimPaletteFor(trimItem);
        List<Integer> paletteColours = trimPalette.getColours().subList(0, maxSupportedLayer).reversed();

        for (int i = 0; i < maxSupportedLayer; i++) {
            String layerPath;
            if(AllTheTrims.getConfig().overrideExisting) {
                layerPath = modelPath.replace("dynamic", "%d_dynamic".formatted(i));
            } else {
                layerPath = modelPath.replace(assetName, "%d_%s".formatted(i, assetName));
            }
            Sprite sprite = atlas.getSprite(modelId.withPath(layerPath));
            VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(vertexConsumers.getBuffer(layer));
            int colour = ColorHelper.Argb.fullAlpha(paletteColours.get(i));
            model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, colour);
        }
    }
}
