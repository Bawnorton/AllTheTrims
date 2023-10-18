package com.bawnorton.allthetrims;

import com.bawnorton.allthetrims.config.ConfigManager;
import com.bawnorton.allthetrims.util.LogWrapper;
import org.slf4j.LoggerFactory;

public class AllTheTrims {
    public static final String MOD_ID = "allthetrims";
    public static final LogWrapper LOGGER = LogWrapper.of(LoggerFactory.getLogger(MOD_ID), "[AllTheTrims]");
    public static final String TRIM_ASSET_NAME = "dynamic";

    public static void init() {
        LOGGER.debug("AllTheTrims Initialized!");
        ConfigManager.loadConfig();
    }
}
