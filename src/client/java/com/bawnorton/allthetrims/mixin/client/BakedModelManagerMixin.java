package com.bawnorton.allthetrims.mixin.client;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.compat.Compat;
import com.bawnorton.allthetrims.json.JsonHelper;
import com.bawnorton.allthetrims.util.DebugHelper;
import com.bawnorton.allthetrims.util.TrimIndexHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.io.*;
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
                if(!model.has("overrides")) {
                    model.add("overrides", new JsonArray());
                }
                JsonArray overrides = model.getAsJsonArray("overrides");

                final Resource finalResource = resource;
                final String finalArmourType = armourType;
                TrimIndexHelper.loopTrimMaterials((item, index) -> {
                    Identifier itemId = Registries.ITEM.getId(item);
                    JsonObject override = new JsonObject();
                    override.addProperty("model", equipmentId.getNamespace() + ":item/" + equipmentId.getPath() + "-att-" + itemId.getPath() + "_trim");
                    JsonObject predicate = new JsonObject();
                    predicate.addProperty("trim_type", index);
                    override.add("predicate", predicate);
                    overrides.add(override);

                    String overrideResourceString;
                    if (equipment instanceof DyeableArmorItem) {
                        overrideResourceString = """
                                {
                                   "parent": "minecraft:item/generated",
                                   "textures": {
                                     "layer0": "%s:item/%s",
                                     "layer1": "minecraft:item/%s_overlay",
                                     "layer2": "minecraft:trims/items/%s_trim_quartz"
                                   }
                                 }
                                """.formatted(equipmentId.getNamespace(), equipmentId.getPath(), equipmentId.getPath(), finalArmourType);
                    } else {
                        overrideResourceString = """
                                {
                                  "parent": "minecraft:item/generated",
                                  "textures": {
                                    "layer0": "%s:item/%s",
                                    "layer1": "minecraft:trims/items/%s_trim_quartz"
                                  }
                                }
                                """.formatted(equipmentId.getNamespace(), equipmentId.getPath(), finalArmourType);
                    }
                    Identifier overrideResourceModelId = new Identifier(equipmentId.getNamespace(), "models/item/" + equipmentId.getPath() + "-att-" + itemId.getPath() + "_trim.json");
                    Resource overrideResource = new Resource(finalResource.getPack(), () -> IOUtils.toInputStream(overrideResourceString, "UTF-8"));
                    original.put(overrideResourceModelId, overrideResource);
                });
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
