package com.bawnorton.allthetrims.mixin.client;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.json.ArmourModel;
import com.bawnorton.allthetrims.json.JsonRepresentable;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.Item;
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
        Set<ArmorItem> armourItems = new HashSet<>();
        Registries.ITEM.forEach(item -> {
            if (item instanceof ArmorItem) armourItems.add((ArmorItem) item);
        });
        for (ArmorItem armourItem : armourItems) {
            Identifier armourId = Registries.ITEM.getId(armourItem);
            Identifier resourceId = new Identifier(armourId.getNamespace(), "models/item/" + armourId.getPath() + ".json");
            Resource resource = original.get(resourceId);
            try (BufferedReader reader = resource.getReader()) {
                ArmourModel model = JsonRepresentable.fromJson(reader, ArmourModel.class);
                List<ArmourModel.Override> overrides = model.overrides;
                if(overrides == null) {
                    overrides = new ArrayList<>();
                    model.overrides = overrides;
                }
                int max = (Registries.ITEM.getIds().size() + 10) * 10;
                float index = 1f / max;
                for (Item item : Registries.ITEM) {
                    if(AllTheTrims.isVanilla(item)) continue;
                    String armourType = switch (armourItem.getSlotType()) {
                        case HEAD -> "helmet";
                        case CHEST -> "chestplate";
                        case LEGS -> "leggings";
                        case FEET -> "boots";
                        case MAINHAND, OFFHAND -> null;
                    };
                    if (armourType == null) {
                        AllTheTrims.LOGGER.warn("Item " + armourId + "'s slot type is not an armour slot type, skipping");
                        continue;
                    }
                    Identifier itemId = Registries.ITEM.getId(item);
                    Map<String, Float> predicate = Map.of("trim_type", index);
                    overrides.add(new ArmourModel.Override(armourId.getNamespace() + ":item/" + armourId.getPath() + "_" + itemId.getPath() + "_trim", predicate));
                    index += 1f / max;

                    String overrideResourceString;
                    if (armourItem instanceof DyeableArmorItem) {
                        overrideResourceString = """
                                {
                                   "parent": "minecraft:item/generated",
                                   "textures": {
                                     "layer0": "%s:item/%s",
                                     "layer1": "minecraft:item/%s_overlay",
                                     "layer2": "minecraft:trims/items/%s_trim_quartz"
                                   }
                                 }
                                """.formatted(armourId.getNamespace(), armourId.getPath(), armourId.getPath(), armourType);
                    } else {
                        overrideResourceString = """
                                {
                                  "parent": "minecraft:item/generated",
                                  "textures": {
                                    "layer0": "%s:item/%s",
                                    "layer1": "minecraft:trims/items/%s_trim_quartz"
                                  }
                                }
                                """.formatted(armourId.getNamespace(), armourId.getPath(), armourType);
                    }
                    Identifier overrideResourceModelId = new Identifier(armourId.getNamespace(), "models/item/" + armourId.getPath() + "_" + itemId.getPath() + "_trim.json");
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
