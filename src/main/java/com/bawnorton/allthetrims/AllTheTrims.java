package com.bawnorton.allthetrims;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AllTheTrims implements ModInitializer {
    public static final String MOD_ID = "allthetrims";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final List<Item> VANILLA_MATERIALS = List.of(
            Items.QUARTZ,
            Items.IRON_INGOT,
            Items.NETHERITE_INGOT,
            Items.REDSTONE,
            Items.COPPER_INGOT,
            Items.GOLD_INGOT,
            Items.EMERALD,
            Items.DIAMOND,
            Items.LAPIS_LAZULI,
            Items.AMETHYST_SHARD
	);

    @Override
    public void onInitialize() {
        LOGGER.info("AllTheTrims Initialized!");
    }

	public static boolean isVanilla(Item item) {
		return VANILLA_MATERIALS.contains(item);
	}
}