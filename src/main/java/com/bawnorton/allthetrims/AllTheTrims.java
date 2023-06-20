package com.bawnorton.allthetrims;

import com.bawnorton.allthetrims.config.Config;
import com.bawnorton.allthetrims.config.ConfigManager;
import com.bawnorton.allthetrims.data.AllTheTrimsTags;
import com.bawnorton.allthetrims.util.LogWrapper;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.Util;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class AllTheTrims implements ModInitializer {
    public static final String MOD_ID = "allthetrims";
    public static final LogWrapper LOGGER = LogWrapper.of(LoggerFactory.getLogger(MOD_ID), "[AllTheTrims]");

    public static final Set<Item> USED_MATERIALS = Util.make(new HashSet<>(), list -> {
        list.add(Items.DIAMOND);
        list.add(Items.IRON_INGOT);
        list.add(Items.COPPER_INGOT);
        list.add(Items.GOLD_INGOT);
        list.add(Items.NETHERITE_INGOT);
        list.add(Items.QUARTZ);
        list.add(Items.EMERALD);
        list.add(Items.REDSTONE);
        list.add(Items.LAPIS_LAZULI);
        list.add(Items.AMETHYST_SHARD);
    });

    public static boolean checkedUsedMaterials = false;

    @Override
    public void onInitialize() {
        LOGGER.info("AllTheTrims Initialized!");
        ConfigManager.loadConfig();
    }

    public static void addUsedAsMaterial(Item item) {
        USED_MATERIALS.add(item);
    }

    public static boolean isUsedAsMaterial(Item item) {
        return USED_MATERIALS.contains(item);
    }

    public static boolean notWhitelisted(Item item) {
        if(Config.getInstance().ignoreWhitelist) return false;
        return !Registries.ITEM.getEntry(item).isIn(AllTheTrimsTags.WHITELIST);
    }
}