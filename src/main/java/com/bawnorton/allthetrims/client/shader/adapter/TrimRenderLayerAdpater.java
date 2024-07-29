package com.bawnorton.allthetrims.client.shader.adapter;

import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import com.bawnorton.allthetrims.client.colour.ARGBColourHelper;
import com.bawnorton.allthetrims.client.compat.Compat;
import com.bawnorton.allthetrims.client.extend.RenderLayer$MultiPhaseParametersExtender;
import com.bawnorton.allthetrims.client.palette.TrimPalette;
import com.bawnorton.allthetrims.client.shader.RenderContext;
import com.bawnorton.allthetrims.client.shader.TrimPalettePhase;
import com.bawnorton.allthetrims.client.shader.TrimShaderManager;
import com.bawnorton.allthetrims.util.MemoizedFunction;
import net.minecraft.client.render.RenderLayer;

public abstract class TrimRenderLayerAdpater {
    private final TrimShaderManager shaderManager = AllTheTrimsClient.getShaderManger();

    protected abstract MemoizedFunction<TrimPalette, RenderLayer> getRenderLayer();

    protected abstract RenderLayer.MultiPhaseParameters.Builder getPhaseParametersBuilder();

    protected RenderLayer.MultiPhaseParameters getPhaseParameters(TrimPalette palette) {
        RenderLayer.MultiPhaseParameters.Builder builder = getPhaseParametersBuilder()
                .program(shaderManager.getProgram());
        RenderLayer.MultiPhaseParameters parameters = builder.build(true);
        ((RenderLayer$MultiPhaseParametersExtender) (Object) parameters).allthetrims$attachTrimPalette(
                new TrimPalettePhase(
                        "trim_palette",
                        () -> shaderManager.setTrimPalette(getPaletteColours(palette)),
                        () -> {}
                )
        );
        return parameters;
    }

    protected RenderContext getContext() {
        return AllTheTrimsClient.getTrimRenderer().getContext();
    }

    protected int[] getPaletteColours(TrimPalette palette) {
        return Compat.getShowMeYourSkinCompat()
                .map(compat -> {
                    int[] colourArr = palette.getColourArr();
                    int[] alphaApplied = new int[colourArr.length];
                    for (int i = 0; i < colourArr.length; i++) {
                        int alpha = compat.getAlpha(getContext().entity(), getContext().trimmed());
                        alphaApplied[i] = ARGBColourHelper.withAlpha(colourArr[i], alpha);
                    }
                    return alphaApplied;

                }).orElse(palette.getColourArr());
    }

    public RenderLayer getRenderLayer(TrimPalette trimPalette) {
        return getRenderLayer().apply(trimPalette);
    }

    public void clearCache() {
        getRenderLayer().clear();
    }
}
