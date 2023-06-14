package com.bawnorton.allthetrims;

import net.fabricmc.api.ClientModInitializer;

public class AllTheTrimsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		AllTheTrims.LOGGER.info("Initializing AllTheTrims Client");
	}
}