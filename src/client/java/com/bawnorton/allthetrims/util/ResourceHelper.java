package com.bawnorton.allthetrims.util;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.compat.Compat;
import com.bawnorton.allthetrims.json.JsonHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.*;

public abstract class ResourceHelper {
    public static List<Resource> replaceTrimMaterials(List<Resource> original, Identifier identifier) {
        if(!identifier.toString().contains("armour_trims")) return original;

        List<Resource> newResources = new ArrayList<>();
        for (Resource resource : original) {
            try (BufferedReader reader = resource.getReader()) {
                JsonObject atlas = JsonHelper.fromJson(reader, JsonObject.class);
                if(!atlas.has("sources")) return original;

                JsonArray sources = atlas.getAsJsonArray("sources");
                if(sources.size() == 0) return original;

                for(JsonElement source: sources) {
                    if(!(source instanceof JsonObject sourceJson)) continue;
                    if(!sourceJson.has("permutations")) continue;
                    if(!sourceJson.has("type")) continue;

                    String type = sourceJson.get("type").getAsString();
                    if(!type.equals("paletted_permutations")) continue;

                    JsonObject permutations = new JsonObject();
                    permutations.addProperty("att-blank", "trims/color_palettes/blank");
                    sourceJson.add("permutations", permutations);
                }
                newResources.add(new Resource(resource.getPack(), () -> IOUtils.toInputStream(JsonHelper.toJson(atlas), "UTF-8")));

                DebugHelper.createDebugFile("atlases", resource.getResourcePackName() + "_armour_trims.json", JsonHelper.toJson(atlas));
            } catch (RuntimeException | IOException e) {
                AllTheTrims.LOGGER.error("Failed to modify trim atlas: " + identifier);
                return original;
            }
        }
        return newResources;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<Resource> getLayeredTrimResource(Optional<Resource> original, ResourceManager resourceManager, Identifier identifier) {
        if(original.isPresent()) return original;

        String path = identifier.getPath();
        Identifier originalIdentifier = identifier.withPath(path.substring(0, path.lastIndexOf('_')) + ".png");
        Optional<Resource> optionalResource = resourceManager.getResource(originalIdentifier);
        if(optionalResource.isEmpty()) return optionalResource;

        int layer = Integer.parseInt(String.valueOf(path.charAt(path.length() - "x.png".length())));
        Resource resource = optionalResource.get();
        try(InputStream inputStream = resource.getInputStream()) {
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            if(bufferedImage == null) {
                throw new RuntimeException("Failed to read image from " + originalIdentifier);
            }
            Color colour = ImageUtil.getNthDarkestColour(bufferedImage, layer);
            BufferedImage newImage = ImageUtil.removeOtherColours(bufferedImage, colour);
            DebugHelper.saveLayeredTexture(newImage, identifier.toString());
            return Optional.of(new Resource(resource.getPack(), () -> ImageUtil.toInputStream(newImage)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<Identifier, Resource>  addTrimModels(Map<Identifier, Resource> original) {
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
                JsonArray overrides = new JsonArray();
                model.add("overrides", overrides);
                JsonObject attOverride = new JsonObject();
                attOverride.addProperty("model", equipmentId.getNamespace() + ":item/" + equipmentId.getPath() + "_att-blank_trim");
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
                                 "layer0": "%1$s:item/%2$s",
                                 "layer1": "minecraft:item/%2$s_overlay",
                                 "layer2": "minecraft:trims/items/%3$s_trim_0_att-blank",
                                 "layer3": "minecraft:trims/items/%3$s_trim_1_att-blank",
                                 "layer4": "minecraft:trims/items/%3$s_trim_2_att-blank",
                                 "layer5": "minecraft:trims/items/%3$s_trim_3_att-blank",
                                 "layer6": "minecraft:trims/items/%3$s_trim_4_att-blank",
                                 "layer7": "minecraft:trims/items/%3$s_trim_5_att-blank",
                                 "layer8": "minecraft:trims/items/%3$s_trim_6_att-blank",
                                 "layer9": "minecraft:trims/items/%3$s_trim_7_att-blank"
                               }
                            }
                            """.formatted(equipmentId.getNamespace(), equipmentId.getPath(), armourType);
                } else {
                    overrideResourceString = """
                            {
                              "parent": "minecraft:item/generated",
                              "textures": {
                                "layer0": "%1$s:item/%2$s",
                                "layer1": "minecraft:trims/items/%3$s_trim_0_att-blank",
                                "layer2": "minecraft:trims/items/%3$s_trim_1_att-blank",
                                "layer3": "minecraft:trims/items/%3$s_trim_2_att-blank",
                                "layer4": "minecraft:trims/items/%3$s_trim_3_att-blank",
                                "layer5": "minecraft:trims/items/%3$s_trim_4_att-blank",
                                "layer6": "minecraft:trims/items/%3$s_trim_5_att-blank",
                                "layer7": "minecraft:trims/items/%3$s_trim_6_att-blank",
                                "layer8": "minecraft:trims/items/%3$s_trim_7_att-blank"
                              }
                            }
                            """.formatted(equipmentId.getNamespace(), equipmentId.getPath(), armourType);
                }

                Identifier overrideResourceModelId = new Identifier(equipmentId.getNamespace(), "models/item/" + equipmentId.getPath() + "_att-blank_trim.json");
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
