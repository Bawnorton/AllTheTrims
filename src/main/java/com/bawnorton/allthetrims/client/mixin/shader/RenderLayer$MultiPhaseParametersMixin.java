package com.bawnorton.allthetrims.client.mixin.shader;

import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import com.bawnorton.allthetrims.client.render.TrimPalettePhase;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderLayer.MultiPhaseParameters.class)
public abstract class RenderLayer$MultiPhaseParametersMixin {
    @Shadow @Final @Mutable
    ImmutableList<RenderPhase> phases;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addTrimPalettePhase(CallbackInfo ci) {
        TrimPalettePhase trimPalette = AllTheTrimsClient.getShaderManager().PHASE_ARG_LOCAL.get();
        phases = ImmutableList.<RenderPhase>builder()
                .addAll(phases)
                .add(trimPalette)
                .build();
    }
}
