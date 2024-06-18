package com.bawnorton.allthetrims.client.mixin.shader;

import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShaderProgram.class)
public abstract class ShaderProgramMixin {
    @Shadow @Nullable
    public abstract GlUniform getUniform(String name);

    @Unique
    private GlUniform allthetrims$trimPalette;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initAdditionalUniforms(CallbackInfo ci) {
        allthetrims$trimPalette = getUniform("TrimPalette");
    }

    @Inject(
            method = "initializeUniforms",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;setupShaderLights(Lnet/minecraft/client/gl/ShaderProgram;)V"
            )
    )
    private void setAdditionalUniforms(CallbackInfo ci) {
        if(allthetrims$trimPalette != null) {
            allthetrims$trimPalette.set(AllTheTrimsClient.getTrimPalette());
        }
    }
}
