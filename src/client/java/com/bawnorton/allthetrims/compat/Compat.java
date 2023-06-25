package com.bawnorton.allthetrims.compat;

import com.bawnorton.allthetrims.compat.client.ShowMeYourSkinCompat;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;

public abstract class Compat {
    public static boolean isYaclLoaded() {
        return FabricLoader.getInstance().isModLoaded("yet_another_config_lib_v3");
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
