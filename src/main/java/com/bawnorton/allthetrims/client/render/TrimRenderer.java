package com.bawnorton.allthetrims.client.render;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import com.bawnorton.allthetrims.client.compat.Compat;
import com.bawnorton.allthetrims.client.compat.iris.IrisCompat;
import com.bawnorton.allthetrims.client.palette.TrimPalette;
import com.bawnorton.allthetrims.client.render.adapter.TrimRendererAdapter;
import com.bawnorton.allthetrims.client.shader.RenderContext;
import com.bawnorton.allthetrims.util.Adaptable;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.MissingSprite;
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
import java.util.List;

public final class TrimRenderer extends Adaptable<TrimRendererAdapter> {
    public boolean isSpriteDynamic(Sprite sprite) {
        return sprite.getContents().getId().getPath().endsWith("_%s".formatted(AllTheTrims.DYNAMIC));
    }

    /**
     * Always used to render overrides and when a shader is enabled.
     * @apiNote User can force the legacy renderer to be used.
     */
    public boolean useLegacyRenderer(Sprite sprite) {
        boolean useLegacyRenderer = AllTheTrimsClient.getConfig().useLegacyRenderer && isSpriteDynamic(sprite); // specified to use it
        useLegacyRenderer |= Compat.getIrisCompat().map(IrisCompat::isUsingShader).orElse(false); // have to use it
        useLegacyRenderer |= AllTheTrimsClient.getConfig().overrideExisting && !isSpriteDynamic(sprite); // overriding existing
        return useLegacyRenderer;
    }

    public RenderLayer getLegacyRenderLayer(Item trimmed, ArmorTrim trim) {
        return getAdapter(trimmed).getLegacyRenderLayer(trim);
    }

    public int getTrimAlpha(RenderContext context) {
        return getAdapter(context.trimmed()).getAlpha(context);
    }

    /**
     * @see #renderTrim(ArmorTrim, Sprite, MatrixStack, VertexConsumerProvider, int, int, int, Identifier, SpriteAtlasTexture, RenderLayer, RenderCallback)
     */
    public void renderTrim(ArmorTrim trim, Sprite sprite, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, int colour, SpriteAtlasTexture atlasTexture, RenderCallback callback) {
        renderTrim(trim, sprite, matrices, vertexConsumers, light, overlay, colour, atlasTexture, null, callback);
    }

    /**
     * @see #renderTrim(ArmorTrim, Sprite, MatrixStack, VertexConsumerProvider, int, int, int, Identifier, SpriteAtlasTexture, RenderLayer, RenderCallback)
     */
    public void renderTrim(ArmorTrim trim, Sprite sprite, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, int colour, Identifier modelId, SpriteAtlasTexture atlasTexture, RenderCallback callback) {
        renderTrim(trim, sprite, matrices, vertexConsumers, light, overlay, colour, modelId, atlasTexture, null, callback);
    }

    /**
     * @see #renderTrim(ArmorTrim, Sprite, MatrixStack, VertexConsumerProvider, int, int, int, Identifier, SpriteAtlasTexture, RenderLayer, RenderCallback)
     */
    public void renderTrim(ArmorTrim trim, Sprite sprite, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, int colour, SpriteAtlasTexture atlasTexture, RenderLayer renderLayer, RenderCallback callback) {
        renderTrim(trim, sprite, matrices, vertexConsumers, light, overlay, colour, getModelId(sprite), atlasTexture, renderLayer, callback);
    }

    /**
     * Calculates how to render a trim then passes it to the {@link RenderCallback}
     * <br>
     * Types of rendering:
     * <ul>
     *  <li><b>Legacy Rendering</b>: Splits the pattern texture into layers and renders each layer of separately with the colour of that layer derived from the trim palette</li>
     *  <li><b>Shader Rendering</b>: Uses a core shader to render the trim. ~8x more performant than Legacy Rendering</li>
     * </ul>
     *
     * @param sprite          The sprite of the trim pattern, if overriding existing is enabled, it will be ignored.
     * @param vertexConsumers Providers for the Legacy and Shader renderers
     * @param modelId         Optionally provide the model to use
     * @param atlasTexture    The atlas to pull the trim texture layers from. Used by Legacy
     * @param renderLayer     Optionally provide a render layer, otherwise the default will be used.
     * @param callback        The renderer. Typically {@link Model#render(MatrixStack, VertexConsumer, int, int, int)}. But can be a {@link Operation} if the call is wrapped.
     */
    public void renderTrim(ArmorTrim trim, Sprite sprite, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, int colour, Identifier modelId, SpriteAtlasTexture atlasTexture, RenderLayer renderLayer, RenderCallback callback) {
        RenderContext context = AllTheTrimsClient.getShaderManger().context;

        if (useLegacyRenderer(sprite)) {
            legacyRenderTrim(context, trim, matrices, vertexConsumers, light, overlay, colour, modelId, atlasTexture, renderLayer, callback);
        } else if (isSpriteDynamic(sprite)) {
            shaderRenderTrim(context, trim, sprite, matrices, vertexConsumers, light, overlay, colour, renderLayer, callback);
        } else {
            callback.render(matrices, sprite.getTextureSpecificVertexConsumer(vertexConsumers.getBuffer(getLegacyRenderLayer(context.trimmed(), trim))), light, overlay, colour);
        }
    }

    public void shaderRenderTrim(RenderContext context, ArmorTrim trim, Sprite sprite, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, int colour, RenderLayer renderLayer, RenderCallback callback) {
        Item material = trim.getMaterial().value().ingredient().value();
        Item trimmed = context.trimmed();
        TrimPalette palette = AllTheTrimsClient.getTrimPalettes().getOrGeneratePalette(material);
        if(renderLayer == null) {
            renderLayer = AllTheTrimsClient.getShaderManger().getTrimRenderLayer(trimmed, palette);
        }
        VertexConsumer vertices = sprite.getTextureSpecificVertexConsumer(vertexConsumers.getBuffer(renderLayer));
        getAdapter(trimmed).render(context, matrices, vertices, light, overlay, ColorHelper.Argb.withAlpha(getTrimAlpha(context), colour), callback);
    }

    public void legacyRenderTrim(RenderContext context, ArmorTrim trim, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, int colour, Identifier modelId, SpriteAtlasTexture atlasTexture, RenderLayer renderLayer, RenderCallback callback) {
        if(modelId.equals(MissingSprite.getMissingSpriteId())) return;

        ArmorTrimMaterial trimMaterial = trim.getMaterial().value();
        Item trimItem = trimMaterial.ingredient().value();
        Item trimmed = context.trimmed();

        if(renderLayer == null) {
            renderLayer = getLegacyRenderLayer(trimmed, trim);
        }

        Identifier patternId = modelId.withPath(path -> "textures/%s.png".formatted(path.substring(0, path.lastIndexOf("_"))));
        int maxSupportedLayer = AllTheTrimsClient.getLayerData().getMaxSupportedLayer(patternId);

        TrimPalette trimPalette = AllTheTrimsClient.getTrimPalettes().getOrGeneratePalette(trimItem);
        List<Integer> paletteColours = trimPalette.getColours().subList(0, maxSupportedLayer).reversed();
        String assetName = getAssetName(trimMaterial);
        TrimRendererAdapter adapter = getAdapter(trimmed);
        int alpha = getTrimAlpha(context);
        for (int i = 0; i < maxSupportedLayer; i++) {
            Identifier layerSpriteId = modelId.withPath(modelId.getPath().replace(assetName, "%d_%s".formatted(i, assetName)));
            Sprite sprite = atlasTexture.getSprite(layerSpriteId);
            VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(vertexConsumers.getBuffer(renderLayer));
            colour = paletteColours.get(i) | alpha << 24;
            adapter.render(context, matrices, vertexConsumer, light, overlay, colour, callback);
        }
    }

    public String getAssetName(ArmorTrimMaterial trimMaterial) {
        String assetName;
        if(AllTheTrimsClient.getConfig().overrideExisting) {
            assetName = AllTheTrims.DYNAMIC;
        } else {
            assetName = trimMaterial.assetName();
        }
        return assetName;
    }

    public Identifier getModelId(Sprite sprite) {
        return sprite.getContents().getId();
    }

    public Identifier getModelId(ArmorTrim trim, RegistryEntry<ArmorMaterial> armourMaterial, boolean leggings) {
        return leggings ? trim.getLeggingsModelId(armourMaterial) : trim.getGenericModelId(armourMaterial);
    }

    public interface RenderCallback {
        void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int colour);
    }
}
