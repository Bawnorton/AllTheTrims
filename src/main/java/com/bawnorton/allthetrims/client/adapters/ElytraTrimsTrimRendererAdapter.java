package com.bawnorton.allthetrims.client.adapters;

import com.bawnorton.runtimetrims.client.compat.Compat;
import com.bawnorton.runtimetrims.client.render.TrimRenderer;
import com.bawnorton.runtimetrims.client.render.adapter.TrimRendererAdapter;
import com.bawnorton.runtimetrims.client.shader.RenderContext;
import dev.kikugie.elytratrims.api.ElytraTrimsAPI;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.trim.ArmorTrim;
import java.util.List;

public final class ElytraTrimsTrimRendererAdapter extends TrimRendererAdapter {
    public static final List<Item> APPLICABLE = ElytraTrimModelLoaderAdapter.APPLICABLE;

    @Override
    public RenderLayer getLegacyRenderLayer(ArmorTrim trim) {
        return ElytraTrimsAPI.getElytraLayer();
    }

    @Override
    public int getAlpha(RenderContext context) {
        return Compat.getShowMeYourSkinCompat()
                .map(compat -> compat.getAlpha(context.entity(), context.trimmed()))
                .orElse(255);
    }

    @Override
    public void render(RenderContext context, MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int colour, TrimRenderer.RenderCallback callback) {
        callback.render(matrices, vertexConsumer, light, overlay, colour);
    }
}
