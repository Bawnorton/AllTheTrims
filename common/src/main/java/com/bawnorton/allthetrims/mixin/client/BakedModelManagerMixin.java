package com.bawnorton.allthetrims.mixin.client;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.Compat;
import com.bawnorton.allthetrims.json.JsonHelper;
import com.bawnorton.allthetrims.json.TrimModelOverrideEntryJson;
import com.bawnorton.allthetrims.json.TrimModelOverrideResourceJson;
import com.bawnorton.allthetrims.util.DebugHelper;
import com.bawnorton.allthetrims.util.TrimMaterialHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

@Mixin(BakedModelManager.class)
public abstract class BakedModelManagerMixin {
    @SuppressWarnings("unused")
    @ModifyExpressionValue(method = "method_45895(Lnet/minecraft/resource/ResourceManager;)Ljava/util/Map;", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceFinder;findResources(Lnet/minecraft/resource/ResourceManager;)Ljava/util/Map;"))
    private static Map<Identifier, Resource> addTrimModels(Map<Identifier, Resource> original) {
        Set<Equipment> equipmentSet = new HashSet<>();
        Registries.ITEM.forEach(item -> {
            if (item instanceof Equipment equipment) equipmentSet.add(equipment);
        });
        allTheTrims$findBuiltinTrims(original);
        for (Equipment equipment : equipmentSet) {
            Identifier equipmentId = Registries.ITEM.getId((Item) equipment);
            if (equipmentId.getNamespace().equals("betterend"))
                continue; // Better End dynamically generates models elsewhere. See betterend package

            Identifier resourceId = new Identifier(equipmentId.getNamespace(), "models/item/" + equipmentId.getPath() + ".json");
            String armourType = switch (equipment.getSlotType()) {
                case HEAD -> "helmet";
                case CHEST -> "chestplate";
                case LEGS -> "leggings";
                case FEET -> "boots";
                case MAINHAND, OFFHAND -> null;
            };
            if (armourType == null) {
                if (equipment instanceof ArmorItem) {
                    AllTheTrims.LOGGER.warn("Armour Item " + equipmentId + "'s slot type is not an armour slot type, skipping");
                } else {
                    AllTheTrims.LOGGER.debug("Item " + equipmentId + " is not an armour item, likely expected, skipping");
                }
                continue;
            }

            Resource resource = original.get(resourceId);
            if (resource == null) {
                AllTheTrims.LOGGER.warn("Could not find resource " + resourceId + " for item " + equipmentId + ", skipping");
                continue;
            }
            try (BufferedReader reader = resource.getReader()) {
                JsonObject model = JsonHelper.fromJsonReader(reader, JsonObject.class);
                allTheTrims$correctNamespace(model, "parent", "minecraft");
                if (!model.has("textures")) {
                    AllTheTrims.LOGGER.warn("Item " + equipmentId + "'s model does not have a textures parameter, skipping");
                    continue;
                }

                JsonObject textures = model.get("textures").getAsJsonObject();
                if (!textures.has("layer0")) {
                    AllTheTrims.LOGGER.warn("Item " + equipmentId + "'s model does not have a layer0 texture, skipping");
                    continue;
                }

                List<String> layers = new ArrayList<>();
                int layer = 0;
                while (true) {
                    allTheTrims$correctNamespace(textures, "layer" + layer, equipmentId.getNamespace());
                    JsonElement layerElement = textures.get("layer" + layer);
                    if (layerElement == null) break;
                    layers.add(layerElement.getAsString());
                    layer++;
                }
                if (layers.isEmpty()) {
                    AllTheTrims.LOGGER.warn("Item " + equipmentId + "'s model does not have any layer textures, skipping");
                    continue;
                }

                String baseLayer = layers.get(0);
                List<TrimModelOverrideEntryJson> overrides;
                if (model.has("overrides")) {
                    overrides = TrimModelOverrideEntryJson.fromJsonArray(model.get("overrides").getAsJsonArray());
                } else {
                    overrides = new ArrayList<>();
                }

                Set<Float> seenIndices = allTheTrims$findExistingTrimOverrides(overrides);

                // add model overrides for each trim type
                // builtin:
                TrimMaterialHelper.forEachBuiltinTrimModelOverride(trimModelOverrideEntryJson -> {
                    TrimModelOverrideEntryJson.ModelPredicate<?> modelPredicate = trimModelOverrideEntryJson.predicate();
                    if (!modelPredicate.isFloatPredicate()) return;

                    float index = modelPredicate.asFloatPredicate().trimType();
                    if (seenIndices.contains(index)) return;

                    String assetName = trimModelOverrideEntryJson.assetName();
                    if (assetName == null) return;

                    overrides.add(allTheTrims$createModelOverrideElement(baseLayer, index, assetName));
                });
                // dynamic:
                overrides.add(allTheTrims$createModelOverrideElement(baseLayer, Float.MAX_VALUE, AllTheTrims.TRIM_ASSET_NAME));

                // order matters when rendering, the first override in the list that matches the predicate is used
                overrides.sort(Comparator.comparingDouble(trimModelEntryJson -> {
                    TrimModelOverrideEntryJson.ModelPredicate<?> modelPredicate = trimModelEntryJson.predicate();
                    if (modelPredicate.isStringPredicate()) return Double.MAX_VALUE;
                    return modelPredicate.asFloatPredicate().trimType();
                }));

                // add override model resources for the model overrides to point to
                List<TrimModelOverrideResourceJson> overrideModels = new ArrayList<>();
                // builtin:
                TrimMaterialHelper.forEachBuiltinTrimModelOverride(trimModelOverrideEntryJson -> {
                    //noinspection ConstantValue
                    if (equipment instanceof ElytraItem && Compat.isElytraTrimsLoaded()) return;

                    TrimModelOverrideEntryJson.ModelPredicate<?> modelPredicate = trimModelOverrideEntryJson.predicate();
                    if (!modelPredicate.isFloatPredicate()) return;

                    float index = modelPredicate.asFloatPredicate().trimType();
                    String assetName = trimModelOverrideEntryJson.assetName();
                    if (assetName == null) return;

                    overrideModels.add(allTheTrims$createModelOverrideResource(model, layers, armourType, assetName));
                });
                // dynamic:
                overrideModels.add(allTheTrims$createModelOverrideResource(model, layers, armourType, AllTheTrims.TRIM_ASSET_NAME));

                // add override models to resource map
                Identifier baseId = new Identifier(baseLayer);
                for (TrimModelOverrideResourceJson overrideModel : overrideModels) {
                    Identifier overrideModelId = baseId.withPath("models/" + baseId.getPath() + "_" + overrideModel.assetName() + "_trim.json");
                    Resource overrideResource = new Resource(resource.getPack(), () -> IOUtils.toInputStream(JsonHelper.toJsonString(overrideModel), "UTF-8"));
                    DebugHelper.createDebugFile("models", equipmentId + "_" + overrideModel.assetName() + "_trim.json", JsonHelper.toJsonString(overrideModel));
                    original.put(overrideModelId, overrideResource);
                }

                // add modified model to resource map
                model.add("overrides", TrimModelOverrideEntryJson.toJsonArray(overrides));
                resource = new Resource(resource.getPack(), () -> IOUtils.toInputStream(JsonHelper.toJsonString(model), "UTF-8"));
                DebugHelper.createDebugFile("models", equipmentId + ".json", JsonHelper.toJsonString(model));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            original.put(resourceId, resource);
        }
        return original;
    }

    @Unique
    private static void allTheTrims$correctNamespace(JsonObject json, String key, String namespace) {
        JsonElement element = json.get(key);
        if (element == null) return;

        String value = element.getAsString();
        if (value.contains(":")) return;

        json.addProperty(key, new Identifier(namespace, value).toString());
    }

    @Unique
    private static void allTheTrims$findBuiltinTrims(Map<Identifier, Resource> resourceMap) {
        Identifier equipmentId = Registries.ITEM.getId(Items.CHAINMAIL_CHESTPLATE);
        Identifier resourceId = new Identifier(equipmentId.getNamespace(), "models/item/" + equipmentId.getPath() + ".json");
        Resource resource = resourceMap.get(resourceId);
        try (BufferedReader reader = resource.getReader()) {
            JsonObject model = JsonHelper.fromJsonReader(reader, JsonObject.class);
            JsonArray overrides = model.get("overrides").getAsJsonArray();
            for (JsonElement override : overrides) {
                try {
                    TrimModelOverrideEntryJson overrideJson = TrimModelOverrideEntryJson.fromJson(override.getAsJsonObject());
                    TrimMaterialHelper.BUILTIN_TRIM_MODEL_OVERRIDES.add(overrideJson);
                } catch (IllegalArgumentException ignored) {
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Unique
    @NotNull
    private static Set<Float> allTheTrims$findExistingTrimOverrides(List<TrimModelOverrideEntryJson> overrides) {
        Set<Float> seenIndices = new HashSet<>();
        for (TrimModelOverrideEntryJson overrideEntry : overrides) {
            TrimModelOverrideEntryJson.ModelPredicate<?> overridePredicate = overrideEntry.predicate();
            if (overridePredicate.isStringPredicate()) continue;

            Float index = overridePredicate.asFloatPredicate().trimType();
            seenIndices.add(index);
        }
        return seenIndices;
    }

    @Unique
    private static TrimModelOverrideResourceJson allTheTrims$createModelOverrideResource(JsonObject model, List<String> layers, String armourType, String assetName) {
        JsonObject overrideResourceJson = new JsonObject();
        overrideResourceJson.addProperty("parent", model.get("parent").getAsString());
        JsonObject overrideTextures = new JsonObject();
        int i;
        for (i = 0; i < layers.size(); i++) {
            overrideTextures.addProperty("layer" + i, layers.get(i));
        }
        if (assetName.equals(AllTheTrims.TRIM_ASSET_NAME)) {
            for (int trimCount = 0; trimCount < 8; trimCount++, i++) {
                overrideTextures.addProperty("layer" + i, "minecraft:trims/items/" + armourType + "_trim_" + trimCount + "_" + assetName);
            }
        } else {
            overrideTextures.addProperty("layer" + i, "minecraft:trims/items/" + armourType + "_trim_" + assetName);
        }
        overrideResourceJson.add("textures", overrideTextures);
        return TrimModelOverrideResourceJson.fromJson(overrideResourceJson, assetName);
    }

    @Unique
    private static TrimModelOverrideEntryJson allTheTrims$createModelOverrideElement(String baseLayer, float index, String assetName) {
        JsonObject override = new JsonObject();
        JsonObject overridePredicate = new JsonObject();
        if (assetName.equals(AllTheTrims.TRIM_ASSET_NAME)) {
            overridePredicate.addProperty("trim_type", assetName);
        } else {
            overridePredicate.addProperty("trim_type", index);
        }
        override.addProperty("model", baseLayer + "_" + assetName + "_trim");
        override.add("predicate", overridePredicate);
        return TrimModelOverrideEntryJson.fromJson(override);
    }
}
