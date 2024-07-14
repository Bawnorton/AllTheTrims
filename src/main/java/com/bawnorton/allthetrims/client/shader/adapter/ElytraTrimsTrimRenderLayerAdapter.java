package com.bawnorton.allthetrims.client.shader.adapter;

import com.bawnorton.allthetrims.client.mixin.accessor.RenderPhaseAccessor;
import com.bawnorton.allthetrims.client.model.item.adapter.ElytraTrimModelLoaderAdapter;
import com.bawnorton.allthetrims.client.palette.TrimPalette;
import com.bawnorton.allthetrims.util.MemoizedFunction;
import com.bawnorton.allthetrims.util.Memoizer;
import dev.kikugie.elytratrims.client.resource.ETAtlasHolder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.item.Item;
import java.util.List;

public final class ElytraTrimsTrimRenderLayerAdapter extends TrimRenderLayerAdpater {
    public static final List<Item> APPLICABLE = ElytraTrimModelLoaderAdapter.APPLICABLE;

    private final MemoizedFunction<TrimPalette, RenderLayer> DYNAMIC_TRANSLUCENT_TRIM_RENDER_LAYER = Memoizer.memoize(palette -> RenderLayer.of(
            "dynamic_elytra_trim",
            VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
            VertexFormat.DrawMode.QUADS,
            1536,
            true,
            true,
            getPhaseParameters(palette)
    ));

    @Override
    protected MemoizedFunction<TrimPalette, RenderLayer> getRenderLayer() {
        return DYNAMIC_TRANSLUCENT_TRIM_RENDER_LAYER;
    }

    protected RenderLayer.MultiPhaseParameters.Builder getPhaseParametersBuilder() {
        return RenderLayer.MultiPhaseParameters.builder()
                .texture(new RenderPhase.Texture(ETAtlasHolder.INSTANCE.getId(), false, false))
                .cull(RenderPhaseAccessor.getDisableCulling())
                .transparency(RenderPhaseAccessor.getTranslucentTransparency())
                .lightmap(RenderPhaseAccessor.getEnableLightmap())
                .overlay(RenderPhaseAccessor.getEnableOverlayColor())
                .layering(RenderPhaseAccessor.getViewOffsetZLayering())
                .depthTest(RenderPhaseAccessor.getLequalDepthTest())
                .writeMaskState(RenderPhaseAccessor.getColorMask());
    }
}
