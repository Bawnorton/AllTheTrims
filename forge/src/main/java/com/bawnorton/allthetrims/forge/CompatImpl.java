package com.bawnorton.allthetrims.forge;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraftforge.fml.ModList;

public class CompatImpl {
    public static boolean isYaclLoaded() {
        return ModList.get().isLoaded("yet_another_config_lib_v3");
    }

    public static RenderLayer getTrimRenderLayer() {
        return TexturedRenderLayers.getArmorTrims();
    }

    public static float getTrimTransparency() {
        return 1.0F;
    }

    public static boolean isDynamicTrimLoaded() {
        return ModList.get().isLoaded("dynamictrim");
    }
}
