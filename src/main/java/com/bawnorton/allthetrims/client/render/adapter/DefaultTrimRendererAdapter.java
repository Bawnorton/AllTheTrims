package com.bawnorton.allthetrims.client.render.adapter;

import com.bawnorton.allthetrims.client.render.TrimRenderer;
import com.bawnorton.allthetrims.client.shader.RenderContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.trim.ArmorTrim;

public final class DefaultTrimRendererAdapter extends TrimRendererAdapter {
    @Override
    public RenderLayer getLegacyRenderLayer(ArmorTrim trim) {
        return TexturedRenderLayers.getArmorTrims(trim.getPattern().value().decal());
    }

    @Override
    public int getAlpha(RenderContext context) {
        return 255;
    }

    @Override
    public void render(RenderContext context, MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int colour, TrimRenderer.RenderCallback callback) {
        callback.render(matrices, vertexConsumer, light, overlay, colour);
    }
}
