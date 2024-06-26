package com.bawnorton.allthetrims.client.compat.elytratrims;


import dev.kikugie.elytratrims.client.render.ETRenderer;
import dev.kikugie.elytratrims.client.resource.ETAtlasHolder;
import net.minecraft.client.render.RenderLayer;

public final class ElytraTrimsCompat {
    public RenderLayer getElytraTrimRenderLayer() {
        return ETRenderer.layer.invoke(ETAtlasHolder.INSTANCE.getId());
    }
}
