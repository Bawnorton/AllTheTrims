package com.bawnorton.allthetrims.client.mixin.shader;

import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import com.bawnorton.allthetrims.client.extend.ShaderProgramExtender;
import com.bawnorton.allthetrims.client.mixin.accessor.GlUniformAccessor;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.nio.IntBuffer;

@Mixin(VertexBuffer.class)
public abstract class VertexBufferMixin {
    //? if <1.20.6 {
    @SuppressWarnings("resource")
    @Inject(
            method = "drawInternal",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;setupShaderLights(Lnet/minecraft/client/gl/ShaderProgram;)V"
            )
    )
    private void setAdditionalUniforms(Matrix4f viewMatrix, Matrix4f projectionMatrix, ShaderProgram program, CallbackInfo ci) {
        if(!(program instanceof ShaderProgramExtender extender)) return;

        GlUniform trimPalette = extender.allthetrims$getTrimPalette();
        GlUniform debug = extender.allthetrims$getDebug();
        if(trimPalette != null) {
            int[] colours = AllTheTrimsClient.getShaderManger().getTrimPalette();
            IntBuffer intData = trimPalette.getIntData();
            intData.position(0);
            for (int i = 0; i < colours.length; i++) {
                intData.put(i, colours[i]);
            }
            ((GlUniformAccessor) trimPalette).callMarkStateDirty();
        }

        if(debug != null) {
            debug.set(AllTheTrimsClient.getConfig().debug ? 1 : 0);
        }
    }
    //?}
}
