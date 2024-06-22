package com.bawnorton.allthetrims.client.api;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import com.bawnorton.allthetrims.client.compat.Compat;
import com.bawnorton.allthetrims.client.compat.iris.IrisCompat;
import com.bawnorton.allthetrims.client.palette.TrimPalette;
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

public final class DynamicTrimRenderer {
    public boolean isSpriteDynamic(Sprite sprite) {
        return sprite.getContents().getId().getPath().endsWith("_%s".formatted(AllTheTrims.DYNAMIC));
    }

    public boolean isSpriteMissing(Sprite sprite) {
        return sprite.getContents().getId().equals(MissingSprite.getMissingSpriteId());
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

    /**
     * Calculates how to render a trim then passes it to the {@link RenderCallback}
     * <br>
     * Types of rendering:
     * <ul>
     *  <li><b>Normal Rendering</b>: Passes the trim back to the render callback without manipulating any provided parameters</li>
     *  <li><b>Legacy Rendering</b>: Splits the pattern texture into layers and renders each layer of separately with the colour of that layer derived from the trim palette</li>
     *  <li><b>Shader Rendering</b>: Uses a core shader to render the trim. ~8x more performant than Legacy Rendering</li>
     * </ul>
     * @param sprite The sprite of the trim pattern, if overriding existing is enabled, it will be ignored.
     * @param model Model of the player, or creature wearing the trim
     * @param vertexConsumer Vertex consumer used by the Normal renderer
     * @param vertexConsumers Providers for the Legacy and Shader renderers
     * @param atlasTexture The atlas to pull the trim texture layers from. Used by Legacy
     * @param callback The renderer. Typically {@link Model#render(MatrixStack, VertexConsumer, int, int, int)}. But can be a {@link Operation} if the call is wrapped.
     */
    public void renderTrim(ArmorTrim trim, Sprite sprite, Model model, MatrixStack matrices, VertexConsumer vertexConsumer, VertexConsumerProvider vertexConsumers, int light, int overlay, SpriteAtlasTexture atlasTexture, RenderCallback callback) {
        renderTrim(trim, sprite, model, matrices, vertexConsumer, vertexConsumers, light, overlay, atlasTexture, null, callback);
    }

    /**
     * @see DynamicTrimRenderer#renderTrim(ArmorTrim, Sprite, Model, MatrixStack, VertexConsumer, VertexConsumerProvider, int, int, SpriteAtlasTexture, RenderCallback)
     * @param modelId optionally provide the model to use
     */
    public void renderTrim(ArmorTrim trim, Sprite sprite, Model model, MatrixStack matrices, VertexConsumer vertexConsumer, VertexConsumerProvider vertexConsumers, int light, int overlay, Identifier modelId, SpriteAtlasTexture atlasTexture, RenderCallback callback) {
        renderTrim(trim, sprite, model, matrices, vertexConsumer, vertexConsumers, light, overlay, modelId, atlasTexture, null, callback);
    }

    /**
     * @see DynamicTrimRenderer#renderTrim(ArmorTrim, Sprite, Model, MatrixStack, VertexConsumer, VertexConsumerProvider, int, int, SpriteAtlasTexture, RenderCallback)
     * @param renderLayer optionally provide a render layer, otherwise the default will be used.
     */
    public void renderTrim(ArmorTrim trim, Sprite sprite, Model model, MatrixStack matrices, VertexConsumer vertexConsumer, VertexConsumerProvider vertexConsumers, int light, int overlay, SpriteAtlasTexture atlasTexture, RenderLayer renderLayer, RenderCallback callback) {
        renderTrim(trim, sprite, model, matrices, vertexConsumer, vertexConsumers, light, overlay, getModelId(sprite), atlasTexture, renderLayer, callback);
    }

    /**
     * @see DynamicTrimRenderer#renderTrim(ArmorTrim, Sprite, Model, MatrixStack, VertexConsumer, VertexConsumerProvider, int, int, SpriteAtlasTexture, RenderCallback)
     * @param modelId optionally provide a model id, otherwise it will be read from the sprite. Be mindful of when override existing is enabled.
     */
    public void renderTrim(ArmorTrim trim, Sprite sprite, Model model, MatrixStack matrices, VertexConsumer vertexConsumer, VertexConsumerProvider vertexConsumers, int light, int overlay, Identifier modelId, SpriteAtlasTexture atlasTexture, RenderLayer renderLayer, RenderCallback callback) {
        if (useLegacyRenderer(sprite)) {
            legacyRenderTrim(trim, model, matrices, vertexConsumers, light, overlay, modelId, atlasTexture, renderLayer, callback);
        } else if (isSpriteDynamic(sprite)) {
            shaderRenderTrim(trim, sprite, model, matrices, vertexConsumers, light, overlay, renderLayer, callback);
        } else {
            callback.render(model, matrices, vertexConsumer, light, overlay, -1);
        }
    }

    public void shaderRenderTrim(ArmorTrim trim, Sprite sprite, Model model, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, RenderLayer renderLayer, RenderCallback callback) {
        Item material = trim.getMaterial().value().ingredient().value();
        TrimPalette palette = AllTheTrimsClient.getTrimPalettes().getTrimPaletteFor(material);
        if(renderLayer == null) {
            renderLayer = AllTheTrimsClient.getShaderManager().getDynamicTrimRenderLayer(palette);
        }
        VertexConsumer vertices = sprite.getTextureSpecificVertexConsumer(vertexConsumers.getBuffer(renderLayer));
        callback.render(model, matrices, vertices, light, overlay, -1);
    }

    public void legacyRenderTrim(ArmorTrim trim, Model model, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Identifier modelId, SpriteAtlasTexture atlasTexture, RenderLayer renderLayer, RenderCallback callback) {
        ArmorTrimMaterial trimMaterial = trim.getMaterial().value();
        Item trimItem = trimMaterial.ingredient().value();

        Identifier patternId = modelId.withPath(path -> "textures/%s.png".formatted(path.substring(0, path.lastIndexOf("_"))));
        int maxSupportedLayer = AllTheTrimsClient.getLayerData().getMaxSupportedLayer(patternId);

        TrimPalette trimPalette = AllTheTrimsClient.getTrimPalettes().getTrimPaletteFor(trimItem);
        List<Integer> paletteColours = trimPalette.getColours().subList(0, maxSupportedLayer).reversed();
        if(renderLayer == null) {
            renderLayer = Compat.getTrimRenderLayer(trim.getPattern().value().decal());
        }
        String assetName = getAssetName(trimMaterial);
        for (int i = 0; i < maxSupportedLayer; i++) {
            Identifier layerSpriteId = modelId.withPath(modelId.getPath().replace(assetName, "%d_%s".formatted(i, assetName)));
            Sprite sprite = atlasTexture.getSprite(layerSpriteId);
            VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(vertexConsumers.getBuffer(renderLayer));
            int colour = ColorHelper.Argb.fullAlpha(paletteColours.get(i));
//            AllTheTrims.LOGGER.info("Rendering: {} with colour {}", layerSpriteId, colour);
            callback.render(model, matrices, vertexConsumer, light, overlay, colour);
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
        void render(Model model, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int colour);
    }
}
