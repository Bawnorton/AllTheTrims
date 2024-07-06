package com.bawnorton.allthetrims.client.shader;

import com.bawnorton.allthetrims.client.palette.TrimPalette;
import com.bawnorton.allthetrims.client.shader.adapter.TrimRenderLayerAdpater;
import com.bawnorton.allthetrims.util.Adaptable;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;

public final class TrimShaderManager extends Adaptable<TrimRenderLayerAdpater> {
    public ShaderProgram renderTypeDynamicTrimProgram;
    private RenderContext context;

    private final RenderPhase.ShaderProgram DYNAMIC_TRIM_PROGRAM = new RenderPhase.ShaderProgram(() -> renderTypeDynamicTrimProgram);

    private int[] trimPalette = new int[8];

    public RenderLayer getTrimRenderLayer(Item trimmed, TrimPalette palette) {
        return getAdapter(trimmed).getRenderLayer(palette);
    }

    public void clearRenderLayerCaches() {
        getAdapters().forEach(TrimRenderLayerAdpater::clearCache);
    }

    public void setContext(Entity entity, Item trimmed) {
        context = new RenderContext(entity, trimmed);
    }

    public RenderContext getContext() {
        return context;
    }

    public void setTrimPalette(int[] trimPalette) {
        RenderSystem.assertOnRenderThread();
        this.trimPalette = trimPalette;
    }

    public int[] getTrimPalette() {
        RenderSystem.assertOnRenderThread();
        return trimPalette;
    }

    public RenderPhase.ShaderProgram getProgram() {
        return DYNAMIC_TRIM_PROGRAM;
    }
}
