package com.bawnorton.allthetrims;

import com.bawnorton.allthetrims.compat.Compat;
import com.bawnorton.allthetrims.compat.client.YACLImpl;
import com.bawnorton.allthetrims.util.ImageUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.awt.*;
import java.net.URI;
import java.util.Optional;

public class AllTheTrimsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		AllTheTrims.LOGGER.info("Initializing AllTheTrims Client");

		//Andrew6rant https://github.com/Andrew6rant provided the proof of concept code for this
		//noinspection SuspiciousToArrayCall
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
			ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
			if (networkHandler == null) return -1;

			DynamicRegistryManager registryManager = networkHandler.getRegistryManager();
			Optional<ArmorTrim> optionalTrim = ArmorTrim.getTrim(registryManager, stack);
			if(optionalTrim.isEmpty()) {
				if(stack.getItem() instanceof DyeableArmorItem dyeableArmorItem) {
					return tintIndex == 0 ? dyeableArmorItem.getColor(stack) : -1;
				}
				return -1;
			}

			Item material = optionalTrim.get().getMaterial().value().ingredient().value();
			Color colour = ImageUtil.getAverageColour(material);
			if(stack.getItem() instanceof DyeableArmorItem dyeableArmorItem) {
				if(tintIndex == 0) {
					return dyeableArmorItem.getColor(stack);
				}
				if(tintIndex == 2) {
					return colour.getRGB();
				}
				return -1;
			}

			return tintIndex == 1 ? colour.getRGB() : -1;
		}, Registries.ITEM.stream().filter(item -> item instanceof Equipment).toArray(Item[]::new));
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