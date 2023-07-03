package com.bawnorton.allthetrims.client;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.Compat;
import com.bawnorton.allthetrims.client.implementation.YACLImpl;
import com.bawnorton.allthetrims.client.util.ImageUtil;
import com.bawnorton.allthetrims.client.util.PaletteHelper;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.*;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AllTheTrimsClient {
    public static void init() {
        AllTheTrims.LOGGER.info("Initializing AllTheTrims Client");

        //Andrew6rant https://github.com/Andrew6rant provided the proof of concept code for this
        //noinspection SuspiciousToArrayCall
        ColorHandlerRegistry.registerItemColors((stack, tintIndex) -> {
            ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
            if (networkHandler == null) return -1;

            DynamicRegistryManager registryManager = networkHandler.getRegistryManager();
            Optional<ArmorTrim> optionalTrim = ArmorTrim.getTrim(registryManager, stack);
            if(optionalTrim.isEmpty()) {
                if(stack.getItem() instanceof DyeableArmorItem dyeableArmorItem) return tintIndex == 0 ? dyeableArmorItem.getColor(stack) : -1;
                return -1;
            }

            ArmorTrimMaterial trimMaterial = optionalTrim.get().getMaterial().value();
            Item trimItem = trimMaterial.ingredient().value();
            String assetName =  trimMaterial.assetName();
            if(stack.getItem() instanceof ArmorItem armourItem) {
                ItemStack[] itemStacks = armourItem.getMaterial().getRepairIngredient().getMatchingStacks();
                List<Item> items = Arrays.stream(itemStacks).map(ItemStack::getItem).toList();
                if (items.contains(trimItem)) {
                    assetName += "_darker";
                }
            }
            List<Color> palette;
            Identifier trimAssetId = new Identifier(Registries.ITEM.getId(trimItem).getNamespace(), assetName);
            if(PaletteHelper.paletteExists(trimAssetId)) {
                palette = PaletteHelper.getPalette(trimAssetId);
            } else {
                palette = PaletteHelper.getPalette(trimItem);
            }
            if(stack.getItem() instanceof DyeableArmorItem dyeableArmorItem) {
                if(tintIndex == 0) return dyeableArmorItem.getColor(stack);
                if(tintIndex >= 2) {
                    return palette.get(MathHelper.clamp(6 - tintIndex, 0, palette.size() - 1)).getRGB();
                }
                return -1;
            }

            if(tintIndex < 1) return -1;
            Color color = palette.get(MathHelper.clamp(6 - tintIndex, 0, palette.size() - 1));
            if(tintIndex == 1) return ImageUtil.changeBrightness(color, 0.6f).getRGB();
            if(tintIndex == 2) return ImageUtil.changeBrightness(color, 0.75f).getRGB();
            if(tintIndex == 3) return ImageUtil.changeBrightness(color, 0.9f).getRGB();
            return color.getRGB();
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
