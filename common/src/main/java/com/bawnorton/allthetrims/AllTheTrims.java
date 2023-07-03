package com.bawnorton.allthetrims;

import com.bawnorton.allthetrims.config.Config;
import com.bawnorton.allthetrims.config.ConfigManager;
import com.bawnorton.allthetrims.data.AllTheTrimsTags;
import com.bawnorton.allthetrims.util.LogWrapper;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import org.slf4j.LoggerFactory;

public class AllTheTrims {
    public static final String MOD_ID = "allthetrims";
    public static final LogWrapper LOGGER = LogWrapper.of(LoggerFactory.getLogger(MOD_ID), "[AllTheTrims]");

    public static void init() {
        LOGGER.info("AllTheTrims Initialized!");
        ConfigManager.loadConfig();
    }

    public static boolean notWhitelisted(Item item) {
        if(Config.getInstance().ignoreWhitelist) return false;
        return !Registries.ITEM.getEntry(item).isIn(AllTheTrimsTags.WHITELIST);
    }
}
