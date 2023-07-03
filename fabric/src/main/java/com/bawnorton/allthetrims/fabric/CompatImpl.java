package com.bawnorton.allthetrims.fabric;

import com.bawnorton.allthetrims.fabric.client.compat.ShowMeYourSkinCompat;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;

public class CompatImpl {
    public static boolean isYaclLoaded() {
        return FabricLoader.getInstance().isModLoaded("yet_another_config_lib_v3");
    }

    public static boolean isElytraTrimsLoaded() {
        return FabricLoader.getInstance().isModLoaded("elytratrims");
    }

    public static boolean isShowMeYourSkinLoaded() {
        return FabricLoader.getInstance().isModLoaded("showmeyourskin");
    }

    public static float getTrimTransparency() {
        if (isShowMeYourSkinLoaded()) return ShowMeYourSkinCompat.getTrimTransparency();
        return 1.0F;
    }

    public static RenderLayer getTrimRenderLayer() {
        if (isShowMeYourSkinLoaded()) return ShowMeYourSkinCompat.getTrimRenderLayer();
        return TexturedRenderLayers.getArmorTrims();
    }
}
