package com.bawnorton.allthetrims;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AllTheTrims implements ModInitializer {
	public static final String MOD_ID = "allthetrims";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("AllTheTrims Initialized!");
	}
}