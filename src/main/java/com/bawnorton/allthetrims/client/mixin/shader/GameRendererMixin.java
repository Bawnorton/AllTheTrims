package com.bawnorton.allthetrims.client.mixin.shader;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.ResourceFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @SuppressWarnings("UnresolvedMixinReference")
    @ModifyReceiver(
            method = "loadPrograms",
            at = @At(
                    value = "INVOKE:LAST",
                    target = "Ljava/util/List;add(Ljava/lang/Object;)Z"
            )
    )
    private List<Pair<ShaderProgram, Consumer<ShaderProgram>>> loadDynamicTrimProgram(List<Pair<ShaderProgram, Consumer<ShaderProgram>>> shaders, Object arg, ResourceFactory factory) throws IOException {
        shaders.add(
                Pair.of(
                        //? if fabric {
                        new net.fabricmc.fabric.impl.client.rendering.FabricShaderProgram(
                        //?} elif neoforge {
                        /*new ShaderProgram(
                        *///?}
                                factory,
                                AllTheTrims.id("rendertype_dynamic_trim"),
                                VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL
                        ),
                        program -> AllTheTrimsClient.getShaderManger().renderTypeDynamicTrimProgram = program
                )
        );
        return shaders;
    }
}
