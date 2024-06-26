package com.bawnorton.allthetrims.client.mixin.shader;

import com.bawnorton.allthetrims.client.extend.RenderLayer$MultiPhaseParametersExtender;
import com.bawnorton.allthetrims.client.shader.TrimPalettePhase;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RenderLayer.MultiPhaseParameters.class)
public abstract class RenderLayer$MultiPhaseParametersMixin implements RenderLayer$MultiPhaseParametersExtender {
    @Shadow @Final @Mutable
    ImmutableList<RenderPhase> phases;

    @Override
    public void allthetrims$attachTrimPalette(TrimPalettePhase trimPalette) {
        phases = ImmutableList.<RenderPhase>builder()
                .addAll(phases)
                .add(trimPalette)
                .build();
    }
}
