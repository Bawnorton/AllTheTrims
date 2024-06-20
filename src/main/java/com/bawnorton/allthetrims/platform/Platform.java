package com.bawnorton.allthetrims.platform;

import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.Path;

//? if fabric {
public final class Platform {
    public static Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
//? }
