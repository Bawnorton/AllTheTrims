package com.bawnorton.allthetrims;

import com.bawnorton.allthetrims.client.config.Config;
import com.bawnorton.allthetrims.client.config.ConfigManager;
import com.bawnorton.allthetrims.client.model.DynamicTrimModelLoader;
import com.bawnorton.allthetrims.client.palette.TrimPalettes;
import com.bawnorton.allthetrims.client.render.DynamicTrimShaderManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.item.trim.ArmorTrimPattern;
import net.minecraft.item.trim.ArmorTrimPatterns;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AllTheTrims {
    public static final String MOD_ID = "allthetrims";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final TrimPalettes trimPalettes = new TrimPalettes();
    private static final DynamicTrimShaderManager shaderManager = new DynamicTrimShaderManager();
    private static final DynamicTrimModelLoader modelLoader = new DynamicTrimModelLoader();
    private static final ConfigManager configManager = new ConfigManager();

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("allthetrims")
                    .then(CommandManager.literal("spawnTheTrims")
                        .executes(context -> {
                            ServerCommandSource source = context.getSource();
                            Vec3d pos = source.getPosition();
                            World world = source.getWorld();

                            BlockPos blockPos = BlockPos.ofFloored(pos).mutableCopy();
                            int maxRowCount = 40;
                            int rowCount = 0;
                            int spacing = 1;
                            RegistryEntry<ArmorTrimPattern> silence = world.getRegistryManager().get(RegistryKeys.TRIM_PATTERN).getEntry(ArmorTrimPatterns.SILENCE).orElseThrow();
                            for (RegistryEntry<ArmorTrimMaterial> material : world.getRegistryManager().get(RegistryKeys.TRIM_MATERIAL).getIndexedEntries()) {
                                if (rowCount <= maxRowCount) {
                                    blockPos = blockPos.north(spacing);
                                } else {
                                    blockPos = blockPos.south(spacing * maxRowCount);
                                    blockPos = blockPos.west(spacing);
                                    rowCount = 0;
                                }
                                rowCount += 1;

                                ArmorStandEntity armorStandEntity = EntityType.ARMOR_STAND.create(world);
                                if(armorStandEntity == null) return 0;

                                armorStandEntity.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                                armorStandEntity.setNoGravity(true);

                                ArmorTrim trim = new ArmorTrim(material, silence, true);
                                ItemStack helmet = Items.NETHERITE_HELMET.getDefaultStack();
                                helmet.set(DataComponentTypes.TRIM, trim);
                                ItemStack chestplate = Items.NETHERITE_CHESTPLATE.getDefaultStack();
                                chestplate.set(DataComponentTypes.TRIM, trim);
                                ItemStack leggings = Items.NETHERITE_LEGGINGS.getDefaultStack();
                                leggings.set(DataComponentTypes.TRIM, trim);
                                ItemStack boots = Items.NETHERITE_BOOTS.getDefaultStack();
                                boots.set(DataComponentTypes.TRIM, trim);

                                armorStandEntity.equipStack(EquipmentSlot.HEAD, helmet);
                                armorStandEntity.equipStack(EquipmentSlot.CHEST, chestplate);
                                armorStandEntity.equipStack(EquipmentSlot.LEGS, leggings);
                                armorStandEntity.equipStack(EquipmentSlot.FEET, boots);

                                world.spawnEntity(armorStandEntity);
                            }

                            return 1;
                        })
                    )
            );
        });
    }

    public static TrimPalettes getTrimPalettes() {
        return trimPalettes;
    }

    public static DynamicTrimShaderManager getShaderManager() {
        return shaderManager;
    }

    public static DynamicTrimModelLoader getModelLoader() {
        return modelLoader;
    }

    public static Config getConfig() {
        return configManager.getOrLoadConfig();
    }

    public static void saveConfig() {
        configManager.saveConfig();
    }
}
