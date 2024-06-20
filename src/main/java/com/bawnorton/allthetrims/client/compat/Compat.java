package com.bawnorton.allthetrims.client.compat;

import com.bawnorton.allthetrims.platform.Platform;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;

public final class Compat {
    public static boolean isIrisLoaded() {
        return Platform.isModLoaded("iris");
    }

    public static boolean isYaclLoaded() {
        return Platform.isModLoaded("yet_another_config_lib_v3");
    }

    public static RenderLayer getTrimRenderLayer(boolean decal) {
        return TexturedRenderLayers.getArmorTrims(decal);
    }
}
