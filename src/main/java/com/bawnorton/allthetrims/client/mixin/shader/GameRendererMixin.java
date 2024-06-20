package com.bawnorton.allthetrims.client.mixin.shader;

import com.bawnorton.allthetrims.AllTheTrims;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.ResourceFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(
            method = "loadPrograms",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/GameRenderer;loadBlurPostProcessor(Lnet/minecraft/resource/ResourceFactory;)V"
            )
    )
    private void loadDynamicTrimProgram(ResourceFactory factory, CallbackInfo ci, @Local(ordinal = 1) List<Pair<ShaderProgram, Consumer<ShaderProgram>>> shaderPrograms) throws IOException {
        shaderPrograms.add(
                Pair.of(
                        new ShaderProgram(factory, "rendertype_dynamic_trim", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
                        program -> AllTheTrims.getShaderManager().renderTypeDynamicTrimProgram = program
                )
        );
    }
}
