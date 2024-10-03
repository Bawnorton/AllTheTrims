package com.bawnorton.allthetrims.client.mixin.shader;

import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import com.bawnorton.allthetrims.client.extend.ShaderProgramExtender;
import com.bawnorton.allthetrims.client.mixin.accessor.GlUniformAccessor;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.nio.IntBuffer;

@Mixin(ShaderProgram.class)
public abstract class ShaderProgramMixin implements ShaderProgramExtender {
    @Shadow @Nullable
    public abstract GlUniform getUniform(String name);

    @Unique
    private GlUniform allthetrims$trimPalette;

    @Unique
    private GlUniform allthetrims$debug;

    @Inject(
            //? if fabric {
            /*method = "<init>",
            *///?} elif neoforge {
            method = "<init>(Lnet/minecraft/resource/ResourceFactory;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/render/VertexFormat;)V",
            //?}
            at = @At("TAIL")
    )
    private void initAdditionalUniforms(CallbackInfo ci) {
        allthetrims$trimPalette = getUniform("allthetrims_TrimPalette");
        allthetrims$debug = getUniform("allthetrims_Debug");
    }

    @Override
    public GlUniform allthetrims$getTrimPalette() {
        return allthetrims$trimPalette;
    }

    @Override
    public GlUniform allthetrims$getDebug() {
        return allthetrims$debug;
    }

    //? if >1.20.6 {
    @Inject(
            method = "initializeUniforms",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;setupShaderLights(Lnet/minecraft/client/gl/ShaderProgram;)V"
            )
    )
    private void setAdditionalUniforms(CallbackInfo ci) {
        if(allthetrims$trimPalette != null) {
            int[] trimPalette = AllTheTrimsClient.getShaderManger().getTrimPalette();
            IntBuffer intData = allthetrims$trimPalette.getIntData();
            intData.position(0);
            for (int i = 0; i < trimPalette.length; i++) {
                intData.put(i, trimPalette[i]);
            }
            ((GlUniformAccessor) allthetrims$trimPalette).callMarkStateDirty();
        }

        if(allthetrims$debug != null) {
            allthetrims$debug.set(AllTheTrimsClient.getConfig().debug ? 1 : 0);
        }
    }
    //?}
}
