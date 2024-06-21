package com.bawnorton.allthetrims.client.compat.elytratrims;

import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import com.bawnorton.allthetrims.client.extend.RenderLayer$MultiPhaseParameters$BuilderExtender;
import com.bawnorton.allthetrims.client.palette.TrimPalette;
import com.bawnorton.allthetrims.client.render.DynamicTrimShaderManager;
import com.bawnorton.allthetrims.client.render.TrimPalettePhase;
import com.bawnorton.allthetrims.util.MemoizedBiFunction;
import com.bawnorton.allthetrims.util.Memoizer;
import dev.kikugie.elytratrims.client.render.ETRenderer;
import dev.kikugie.elytratrims.client.resource.ETAtlasHolder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public final class ElytraTrimsCompat {
    public final MemoizedBiFunction<Identifier, TrimPalette, RenderLayer> DYNAMIC_ELYTRA_TRIM_RENDER_LAYER = Memoizer.memoize((elytraTexture, palette) -> RenderLayer.of(
            "dynamic_elytra_trim",
            VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
            VertexFormat.DrawMode.QUADS,
            1536,
            true,
            true,
            getPhaseParameters(elytraTexture, palette)
    ));

    private RenderLayer.MultiPhaseParameters getPhaseParameters(Identifier elytraTexture, TrimPalette palette) {
        DynamicTrimShaderManager shaderManager = AllTheTrimsClient.getShaderManager();
        RenderLayer.MultiPhaseParameters.Builder builder = RenderLayer.MultiPhaseParameters.builder()
                .program(shaderManager.DYNAMIC_TRIM_PROGRAM)
                .texture(new RenderPhase.Texture(elytraTexture, false, false))
                .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
                .cull(RenderPhase.DISABLE_CULLING)
                .lightmap(RenderPhase.ENABLE_LIGHTMAP)
                .overlay(RenderPhase.ENABLE_OVERLAY_COLOR)
                .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                .depthTest(RenderPhase.LEQUAL_DEPTH_TEST)
                .writeMaskState(RenderPhase.COLOR_MASK);
        ((RenderLayer$MultiPhaseParameters$BuilderExtender) builder).allthetrims$trimPalette(new TrimPalettePhase(
                "trim_palette",
                () -> shaderManager.setTrimPalette(palette.getColourArr()),
                () -> {}
        ));
        return builder.build(true);
    }

    public RenderLayer getDynamicElytraTrimRenderLayer(Identifier elytraTexture, TrimPalette palette) {
        return DYNAMIC_ELYTRA_TRIM_RENDER_LAYER.apply(elytraTexture, palette);
    }

    public RenderLayer getElytraTrimRenderLayer() {
        return ETRenderer.layer.invoke(ETAtlasHolder.INSTANCE.getId());
    }

    public void clearRenderLayerCache() {
        DYNAMIC_ELYTRA_TRIM_RENDER_LAYER.clear();
    }
}
