package com.bawnorton.allthetrims.client.model.item;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import com.bawnorton.allthetrims.client.debug.Debugger;
import com.bawnorton.allthetrims.client.model.item.adapter.TrimModelLoaderAdapter;
import com.bawnorton.allthetrims.client.model.item.json.ModelOverride;
import com.bawnorton.allthetrims.client.model.item.json.TextureLayers;
import com.bawnorton.allthetrims.client.model.item.json.TrimmableItemModel;
import com.bawnorton.allthetrims.client.render.LayerData;
import com.bawnorton.allthetrims.util.Adaptable;
import com.bawnorton.allthetrims.versioned.VIdentifier;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ItemTrimModelLoader extends Adaptable<TrimModelLoaderAdapter> {
    private static final Pattern itemIdPattern = Pattern.compile("^models/item/(.+)?(?=.json).json$");
    private final JsonParser jsonParser;
    private final LayerData layerData;

    public ItemTrimModelLoader(LayerData layerData) {
        this.layerData = layerData;
        this.jsonParser = new JsonParser();
    }

    public Map<Identifier, Resource> loadModels(Map<Identifier, Resource> loadedModels) {
        Map<Identifier, Resource> extendedModels = new HashMap<>(loadedModels);
        List<TrimmableResource> trimmableResources = findTrimmableResources(loadedModels);
        for(TrimmableResource trimmableResource : trimmableResources) {
            TrimmableItemModel itemModel = jsonParser.fromResource(trimmableResource.resource(), TrimmableItemModel.class);
            if(itemModel == null) continue;
            if(itemModel.textures == null) itemModel.textures = TextureLayers.empty();
            if(itemModel.overrides == null) itemModel.overrides = new ArrayList<>();

            Identifier modelId = trimmableResource.modelId().withSuffixedPath("_%s_trim".formatted(AllTheTrims.DYNAMIC));

            if(AllTheTrimsClient.getConfig().overrideExisting) {
                itemModel.overrides.clear();
            }

            itemModel.addOverride(ModelOverride.builder()
                    .withModel(modelId.toString())
                    .withPredicate(jsonParser.toJsonObject(TrimModelPredicate.of(AllTheTrims.MODEL_INDEX)))
                    .build());

            itemModel.overrides.sort(Comparator.comparing(override -> {
                JsonObject predicate = override.predicate;
                if(predicate.has("trim_type")) {
                    return predicate.get("trim_type").getAsFloat();
                } else if (predicate.has("minecraft:trim_type")) {
                    return predicate.get("minecraft:trim_type").getAsFloat();
                }
                return 0f;
            }));

            Resource newResource = jsonParser.toResource(trimmableResource.resource().getPack(), itemModel);
            extendedModels.put(trimmableResource.resourceId(), newResource);

            Resource overrideResource = createModelOverride(itemModel, trimmableResource);
            Identifier overrideResourceId = modelId.withPrefixedPath("models/").withSuffixedPath(".json");
            extendedModels.put(overrideResourceId, overrideResource);

            Debugger.createJson("resources/%s".formatted(trimmableResource.resourceId()), newResource);
            Debugger.createJson("resources/%s".formatted(overrideResourceId), overrideResource);
        }
        return extendedModels;
    }

    public void loadModels(Identifier id, Resource resource, BiConsumer<Identifier, Resource> loadedModelConsumer) {
        loadModels(Map.of(id, resource)).forEach(loadedModelConsumer);
    }

    /**
     * Generates a list of for valid trimmable item
     */
    private List<TrimmableResource> findTrimmableResources(Map<Identifier, Resource> loadedModels) {
        List<TrimmableResource> trimmableResources = new ArrayList<>();
        for (Identifier resourceId : loadedModels.keySet()) {
            String resourcePath = resourceId.getPath();
            Matcher matcher = itemIdPattern.matcher(resourcePath);
            if (!matcher.matches()) continue;

            String itemPath = matcher.group(1);
            Identifier itemId = VIdentifier.of(resourceId.getNamespace(), itemPath);
            Item item = Registries.ITEM.get(itemId);
            if (item == Items.AIR) continue;
            if (!getAdapter(item).canTrim(item)) continue;

            trimmableResources.add(new TrimmableResource(item, resourceId, loadedModels.get(resourceId)));
        }
        return trimmableResources;
    }

    private Resource createModelOverride(TrimmableItemModel overriden, TrimmableResource trimmableResource) {
        List<String> layers = new ArrayList<>(overriden.textures.layers);

        Item trimmable = trimmableResource.item();
        layerData.setTrimStartLayer(trimmable, layers.size());

        TrimModelLoaderAdapter adapter = getAdapter(trimmable);
        int layerCount = adapter.getLayerCount(trimmable);

        for(int i = 0; i < layerCount; i++) {
            layers.add(adapter.getLayerName(trimmable, i));
        }
        TrimmableItemModel itemModel = TrimmableItemModel.builder()
                .parent(overriden.parent)
                .textures(TextureLayers.of(layers))
                .build();

        return jsonParser.toResource(trimmableResource.resource().getPack(), itemModel);
    }
}
