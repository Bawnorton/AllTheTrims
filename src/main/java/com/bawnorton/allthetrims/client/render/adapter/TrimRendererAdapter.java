package com.bawnorton.allthetrims.client.render.adapter;

import com.bawnorton.allthetrims.client.render.TrimRenderer;
import com.bawnorton.allthetrims.client.shader.RenderContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.trim.ArmorTrim;

public abstract class TrimRendererAdapter {
    /**
     * Render layer to use when rendering trims through the legacy renderer.<br>
     * This is used when {@link TrimRenderer#useLegacyRenderer} passes
     */
    public abstract RenderLayer getLegacyRenderLayer(ArmorTrim trim);

    /**
     * Alpha to render the trim at.
     * @return alpha in 0-255 range
     */
    public abstract int getAlpha(RenderContext context);

    /**
     * Entrypoint to modify how the trim renders.
     * @apiNote Must call {@link TrimRenderer.RenderCallback#render}
     */
    public abstract void render(RenderContext context, MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int colour, TrimRenderer.RenderCallback callback);
}
