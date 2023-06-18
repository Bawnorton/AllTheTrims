package com.bawnorton.allthetrims.mixin.client;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.json.ArmourModelJson;
import com.bawnorton.allthetrims.json.JsonRepresentable;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

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
            if(FabricLoader.getInstance().isModLoaded("elytratrims") && Registries.ITEM.get(equipmentId) == Items.ELYTRA) armourType = "elytra";
            Resource resource = original.get(resourceId);
            try (BufferedReader reader = resource.getReader()) {
                ArmourModelJson model = JsonRepresentable.fromJson(reader, ArmourModelJson.class);
                List<ArmourModelJson.Override> overrides = model.overrides;
                if (overrides == null) {
                    overrides = new ArrayList<>();
                    model.overrides = overrides;
                }
                int max = (Registries.ITEM.getIds().size() + 10) * 10;
                float index = 1f / max;
                for (Item item : Registries.ITEM) {
                    if (AllTheTrims.isUsedAsMaterial(item)) continue;
                    Identifier itemId = Registries.ITEM.getId(item);
                    Map<String, Float> predicate = Map.of("trim_type", index);
                    overrides.add(new ArmourModelJson.Override(equipmentId.getNamespace() + ":item/" + equipmentId.getPath() + "-att-" + itemId.getPath() + "_trim", predicate));
                    index += 1f / max;

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
                                """.formatted(equipmentId.getNamespace(), equipmentId.getPath(), equipmentId.getPath(), armourType);
                    } else {
                        overrideResourceString = """
                                {
                                  "parent": "minecraft:item/generated",
                                  "textures": {
                                    "layer0": "%s:item/%s",
                                    "layer1": "minecraft:trims/items/%s_trim_quartz"
                                  }
                                }
                                """.formatted(equipmentId.getNamespace(), equipmentId.getPath(), armourType);
                    }
                    Identifier overrideResourceModelId = new Identifier(equipmentId.getNamespace(), "models/item/" + equipmentId.getPath() + "-att-" + itemId.getPath() + "_trim.json");
                    Resource overrideResource = new Resource(resource.getPack(), () -> IOUtils.toInputStream(overrideResourceString, "UTF-8"));
                    original.put(overrideResourceModelId, overrideResource);
                }
                resource = new Resource(resource.getPack(), model::toInputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            original.put(resourceId, resource);
        }
        return original;
    }
}
