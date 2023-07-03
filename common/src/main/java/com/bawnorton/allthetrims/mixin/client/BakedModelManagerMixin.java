package com.bawnorton.allthetrims.mixin.client;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.Compat;
import com.bawnorton.allthetrims.json.JsonHelper;
import com.bawnorton.allthetrims.util.DebugHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mixin(BakedModelManager.class)
public abstract class BakedModelManagerMixin {
    @ModifyExpressionValue(method = "method_45895(Lnet/minecraft/resource/ResourceManager;)Ljava/util/Map;", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceFinder;findResources(Lnet/minecraft/resource/ResourceManager;)Ljava/util/Map;"))
    private static Map<Identifier, Resource> addTrimModels(Map<Identifier, Resource> original) {
        Set<Equipment> equipmentSet = new HashSet<>();
        Registries.ITEM.forEach(item -> {
            if (item instanceof Equipment equipment) equipmentSet.add(equipment);
        });
        for (Equipment equipment : equipmentSet) {
            Identifier equipmentId = Registries.ITEM.getId((Item) equipment);
            if(equipmentId.getNamespace().equals("betterend")) continue; // Better End dynamically generates models elsewhere. See betterend package

            Identifier resourceId = new Identifier(equipmentId.getNamespace(), "models/item/" + equipmentId.getPath() + ".json");
            String armourType = switch (equipment.getSlotType()) {
                case HEAD -> "helmet";
                case CHEST -> "chestplate";
                case LEGS -> "leggings";
                case FEET -> "boots";
                case MAINHAND, OFFHAND -> null;
            };
            if (armourType == null) {
                AllTheTrims.LOGGER.warn("Item " + equipmentId + "'s slot type is not an armour slot type, skipping");
                continue;
            }
            if(Registries.ITEM.get(equipmentId) instanceof ElytraItem) {
                if(Compat.isElytraTrimsLoaded()) {
                    armourType = "elytra";
                } else {
                    AllTheTrims.LOGGER.warn("Item " + equipmentId + " is an elytra, but elytratrims is not loaded, skipping");
                    continue;
                }
            }
            Resource resource = original.get(resourceId);
            if(resource == null) {
                AllTheTrims.LOGGER.warn("Could not find resource " + resourceId + " for item " + equipmentId + ", skipping");
                continue;
            }
            try (BufferedReader reader = resource.getReader()) {
                JsonObject model = JsonHelper.fromJson(reader, JsonObject.class);
                if (!model.has("textures")) {
                    AllTheTrims.LOGGER.warn("Item " + equipmentId + "'s model does not have a textures parameter, skipping");
                    continue;
                }

                JsonObject textures = model.get("textures").getAsJsonObject();
                if (!textures.has("layer0")) {
                    AllTheTrims.LOGGER.warn("Item " + equipmentId + "'s model does not have a layer0 texture, skipping");
                    continue;
                }

                String baseTexture = textures.get("layer0").getAsString();
                JsonArray overrides = new JsonArray();
                model.add("overrides", overrides);
                JsonObject attOverride = new JsonObject();
                attOverride.addProperty("model", baseTexture + "_att-blank_trim");
                JsonObject predicate = new JsonObject();
                predicate.addProperty("trim_type", 0.099);
                attOverride.add("predicate", predicate);
                overrides.add(attOverride);

                String overrideResourceString;
                if (equipment instanceof DyeableArmorItem) {
                    overrideResourceString = """
                            {
                               "parent": "minecraft:item/generated",
                               "textures": {
                                 "layer0": "%1$s",
                                 "layer1": "%1$s_overlay",
                                 "layer2": "minecraft:trims/items/%2$s_trim_0_att-blank",
                                 "layer3": "minecraft:trims/items/%2$s_trim_1_att-blank",
                                 "layer4": "minecraft:trims/items/%2$s_trim_2_att-blank",
                                 "layer5": "minecraft:trims/items/%2$s_trim_3_att-blank",
                                 "layer6": "minecraft:trims/items/%2$s_trim_4_att-blank",
                                 "layer7": "minecraft:trims/items/%2$s_trim_5_att-blank",
                                 "layer8": "minecraft:trims/items/%2$s_trim_6_att-blank",
                                 "layer9": "minecraft:trims/items/%2$s_trim_7_att-blank"
                               }
                            }
                            """.formatted(baseTexture, armourType);
                } else {
                    overrideResourceString = """
                            {
                              "parent": "minecraft:item/generated",
                              "textures": {
                                "layer0": "%1$s",
                                "layer1": "minecraft:trims/items/%2$s_trim_0_att-blank",
                                "layer2": "minecraft:trims/items/%2$s_trim_1_att-blank",
                                "layer3": "minecraft:trims/items/%2$s_trim_2_att-blank",
                                "layer4": "minecraft:trims/items/%2$s_trim_3_att-blank",
                                "layer5": "minecraft:trims/items/%2$s_trim_4_att-blank",
                                "layer6": "minecraft:trims/items/%2$s_trim_5_att-blank",
                                "layer7": "minecraft:trims/items/%2$s_trim_6_att-blank",
                                "layer8": "minecraft:trims/items/%2$s_trim_7_att-blank"
                              }
                            }
                            """.formatted(baseTexture, armourType);
                }

                Identifier baseId = new Identifier(baseTexture);
                Identifier overrideResourceModelId = baseId.withPath("models/" + baseId.getPath() + "_att-blank_trim.json");
                Resource overrideResource = new Resource(resource.getPack(), () -> IOUtils.toInputStream(overrideResourceString, "UTF-8"));
                original.put(overrideResourceModelId, overrideResource);
                DebugHelper.createDebugFile("models", equipmentId + "_att-blank_trim.json", overrideResourceString);

                resource = new Resource(resource.getPack(), () -> IOUtils.toInputStream(JsonHelper.toJson(model), "UTF-8"));
                DebugHelper.createDebugFile("models", equipmentId + ".json", JsonHelper.toJson(model));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            original.put(resourceId, resource);
        }
        return original;
    }
}