package com.bawnorton.allthetrims.client;

import com.bawnorton.allthetrims.client.extend.RenderLayer$MultiPhaseParameters$BuilderExtender;
import com.bawnorton.allthetrims.client.palette.TrimPalette;
import com.bawnorton.allthetrims.client.render.TrimPalettePhase;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Util;
import java.util.function.Function;

import static net.minecraft.client.render.TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE;

public final class AllTheTrimsClient {
    private static float[] trimPalette = new float[]{0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
    public static ShaderProgram renderTypeDynamicTrimProgram;

    public static final ThreadLocal<TrimPalettePhase> PHASE_ARG_LOCAL = new ThreadLocal<>();
    public static final RenderPhase.ShaderProgram DYNAMIC_TRIM_PROGRAM = new RenderPhase.ShaderProgram(() -> renderTypeDynamicTrimProgram);
    public static final Function<TrimPalette, RenderLayer> DYNAMIC_TRIM_RENDER_LAYER = Util.memoize(palette -> RenderLayer.of(
            "dynamic_trim",
            VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
            VertexFormat.DrawMode.QUADS,
            1536,
            true,
            false,
            getPhaseParameters(palette)
    ));

    private static RenderLayer.MultiPhaseParameters getPhaseParameters(TrimPalette palette) {
        RenderLayer.MultiPhaseParameters.Builder builder = RenderLayer.MultiPhaseParameters.builder()
                .program(DYNAMIC_TRIM_PROGRAM)
                .texture(new RenderPhase.Texture(ARMOR_TRIMS_ATLAS_TEXTURE, false, false))
                .transparency(RenderPhase.NO_TRANSPARENCY)
                .cull(RenderPhase.DISABLE_CULLING)
                .lightmap(RenderPhase.ENABLE_LIGHTMAP)
                .overlay(RenderPhase.ENABLE_OVERLAY_COLOR)
                .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                .depthTest(RenderPhase.LEQUAL_DEPTH_TEST);
        ((RenderLayer$MultiPhaseParameters$BuilderExtender) builder).allthetrims$trimPalette(new TrimPalettePhase(
                "trim_palette",
                () -> setTrimPalette(palette.getFloatArray()),
                () -> {}
        ));
        return builder.build(true);
    }

    public static RenderLayer getDynamicTrimRenderLayer(TrimPalette palette) {
        return DYNAMIC_TRIM_RENDER_LAYER.apply(palette);
    }

    public static void setTrimPalette(float[] trimPalette) {
        RenderSystem.assertOnRenderThread();
        AllTheTrimsClient.trimPalette = trimPalette;
    }

    public static float[] getTrimPalette() {
        RenderSystem.assertOnRenderThread();
        return trimPalette;
    }
}
