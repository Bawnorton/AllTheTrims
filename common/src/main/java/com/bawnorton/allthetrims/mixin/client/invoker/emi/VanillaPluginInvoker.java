package com.bawnorton.allthetrims.mixin.client.invoker.emi;

import dev.emi.emi.VanillaPlugin;
import dev.emi.emi.api.render.EmiRenderable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Invoker;

@Pseudo
@Mixin(VanillaPlugin.class)
public interface VanillaPluginInvoker {
    @Invoker(remap=false)
    static EmiRenderable invokeSimplifiedRenderer(int u, int v) {
        throw new AssertionError();
    }
}
