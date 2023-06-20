package com.bawnorton.allthetrims;

import com.bawnorton.allthetrims.compat.Compat;
import com.bawnorton.allthetrims.compat.YACLImpl;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.net.URI;

public class AllTheTrimsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		AllTheTrims.LOGGER.info("Initializing AllTheTrims Client");
	}

	public static Screen getConfigScreen(Screen parent) {
		if (Compat.isYaclLoaded()) {
			return YACLImpl.getScreen(parent);
		} else {
			return new ConfirmScreen((result) -> {
				if (result) {
					Util.getOperatingSystem().open(URI.create("https://modrinth.com/mod/yacl/versions"));
				}
				MinecraftClient.getInstance().setScreen(parent);
			}, Text.of("Yet Another Config Lib not installed!"), Text.of("YACL 3 is required to edit the config in game, would you like to install YACL 3?"), ScreenTexts.YES, ScreenTexts.NO);
		}
	}
}