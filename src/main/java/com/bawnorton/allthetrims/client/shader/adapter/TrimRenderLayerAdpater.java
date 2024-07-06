package com.bawnorton.allthetrims.client.shader.adapter;

import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import com.bawnorton.allthetrims.client.compat.Compat;
import com.bawnorton.allthetrims.client.compat.showmeyourskin.ShowMeYourSkinCompat;
import com.bawnorton.allthetrims.client.extend.RenderLayer$MultiPhaseParametersExtender;
import com.bawnorton.allthetrims.client.palette.TrimPalette;
import com.bawnorton.allthetrims.client.shader.RenderContext;
import com.bawnorton.allthetrims.client.shader.TrimPalettePhase;
import com.bawnorton.allthetrims.client.shader.TrimShaderManager;
import com.bawnorton.allthetrims.util.MemoizedFunction;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.ColorHelper;

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
        return AllTheTrimsClient.getShaderManger().getContext();
    }

    protected int[] getPaletteColours(TrimPalette palette) {
        int[] colourArr = palette.getColourArr();
        ShowMeYourSkinCompat compat = Compat.getShowMeYourSkinCompat().orElse(null);
        if (compat == null) {
            return colourArr;
        } else {
            colourArr = palette.getColourArr();
            int[] alphaApplied = new int[colourArr.length];
            for (int i = 0; i < colourArr.length; i++) {
                alphaApplied[i] = ColorHelper.Argb.withAlpha(compat.getAlpha(getContext().entity(), getContext().trimmed()), colourArr[i]);
            }
            return alphaApplied;
        }
    }

    public RenderLayer getRenderLayer(TrimPalette trimPalette) {
        return getRenderLayer().apply(trimPalette);
    }

    public void clearCache() {
        getRenderLayer().clear();
    }
}
