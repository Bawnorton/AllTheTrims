package com.bawnorton.allthetrims;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.render.RenderLayer;

public abstract class Compat {
    @ExpectPlatform
    public static boolean isYaclLoaded() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isElytraTrimsLoaded() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static RenderLayer getTrimRenderLayer() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static float getTrimTransparency() {
        throw new AssertionError();
    }
}
