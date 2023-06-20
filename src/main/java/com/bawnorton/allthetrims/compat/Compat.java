package com.bawnorton.allthetrims.compat;

import net.fabricmc.loader.api.FabricLoader;

public abstract class Compat {
    public static boolean isYaclLoaded() {
        return FabricLoader.getInstance().isModLoaded("yet_another_config_lib_v3");
    }

    public static boolean isElytraTrimsLoaded() {
        return FabricLoader.getInstance().isModLoaded("elytratrims");
    }
}
