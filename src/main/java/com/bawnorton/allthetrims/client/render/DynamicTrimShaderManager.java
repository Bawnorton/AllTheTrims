package com.bawnorton.allthetrims.client.render;

import com.bawnorton.allthetrims.client.extend.RenderLayer$MultiPhaseParameters$BuilderExtender;
import com.bawnorton.allthetrims.client.mixin.accessor.RenderPhaseAccessor;
import com.bawnorton.allthetrims.client.palette.TrimPalette;
import com.bawnorton.allthetrims.util.MemoizedFunction;
import com.bawnorton.allthetrims.util.Memoizer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

import static net.minecraft.client.render.TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE;

public final class DynamicTrimShaderManager {
    private boolean exists;

    public DynamicTrimShaderManager() {
        if(exists) {
            throw new IllegalStateException("Trim shader manager has already been initialized.");
        }
        exists = true;
    }

    private int[] trimPalette = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
    public ShaderProgram renderTypeDynamicTrimProgram;

    public final ThreadLocal<TrimPalettePhase> PHASE_ARG_LOCAL = new ThreadLocal<>();
    public final RenderPhase.ShaderProgram DYNAMIC_TRIM_PROGRAM = new RenderPhase.ShaderProgram(() -> renderTypeDynamicTrimProgram);
    public final MemoizedFunction<TrimPalette, RenderLayer> DYNAMIC_TRIM_RENDER_LAYER = Memoizer.memoize(palette -> RenderLayer.of(
            "dynamic_trim",
            VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
            VertexFormat.DrawMode.QUADS,
            1536,
            true,
            false,
            getPhaseParameters(palette)
    ));

    private RenderLayer.MultiPhaseParameters getPhaseParameters(TrimPalette palette) {
        RenderLayer.MultiPhaseParameters.Builder builder = RenderLayer.MultiPhaseParameters.builder()
                .program(DYNAMIC_TRIM_PROGRAM)
                .texture(new RenderPhase.Texture(ARMOR_TRIMS_ATLAS_TEXTURE, false, false))
                //? if fabric {
                .transparency(RenderPhase.NO_TRANSPARENCY)
                .cull(RenderPhase.DISABLE_CULLING)
                .lightmap(RenderPhase.ENABLE_LIGHTMAP)
                .overlay(RenderPhase.ENABLE_OVERLAY_COLOR)
                .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                .depthTest(RenderPhase.LEQUAL_DEPTH_TEST);
                //? } elif neoforge {
                /*.transparency(RenderPhaseAccessor.getNoTransparency())
                .cull(RenderPhaseAccessor.getDisableCulling())
                .lightmap(RenderPhaseAccessor.getEnableLightmap())
                .overlay(RenderPhaseAccessor.getEnableOverlayColor())
                .layering(RenderPhaseAccessor.getViewOffsetZLayering())
                .depthTest(RenderPhaseAccessor.getLequalDepthTest());
                *///? }
        ((RenderLayer$MultiPhaseParameters$BuilderExtender) builder).allthetrims$trimPalette(new TrimPalettePhase(
                "trim_palette",
                () -> setTrimPalette(palette.getColourArr()),
                () -> {}
        ));
        return builder.build(true);
    }

    public RenderLayer getDynamicTrimRenderLayer(TrimPalette palette) {
        return DYNAMIC_TRIM_RENDER_LAYER.apply(palette);
    }

    public void clearRenderLayerCache() {
        DYNAMIC_TRIM_RENDER_LAYER.clear();
    }

    public void setTrimPalette(int[] trimPalette) {
        RenderSystem.assertOnRenderThread();
        this.trimPalette = trimPalette;
    }

    public int[] getTrimPalette() {
        RenderSystem.assertOnRenderThread();
        return trimPalette;
    }
}
