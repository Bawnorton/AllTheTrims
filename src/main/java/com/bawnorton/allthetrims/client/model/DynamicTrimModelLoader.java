package com.bawnorton.allthetrims.client.model;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import com.bawnorton.allthetrims.client.debug.Debugger;
import com.bawnorton.allthetrims.client.model.adapter.TrimModelLoaderAdapter;
import com.bawnorton.allthetrims.client.model.adapter.VanillaTrimModelLoaderAdapter;
import com.bawnorton.allthetrims.client.model.json.ModelOverride;
import com.bawnorton.allthetrims.client.model.json.TextureLayers;
import com.bawnorton.allthetrims.client.model.json.TrimModelPredicate;
import com.bawnorton.allthetrims.client.model.json.TrimmableItemModel;
import com.bawnorton.allthetrims.client.render.LayerData;
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
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class DynamicTrimModelLoader {
    private static final Pattern itemIdPattern = Pattern.compile("^models/item/(.+)?(?=.json).json$");
    private final ResourceParser resourceParser;
    private final LayerData layerData;
    private final Map<Identifier, TrimModelLoaderAdapter> adapters = new HashMap<>();
    private final VanillaTrimModelLoaderAdapter vanillaAdapter = new VanillaTrimModelLoaderAdapter();

    public DynamicTrimModelLoader(LayerData layerData) {
        this.layerData = layerData;
        this.resourceParser = new ResourceParser();
    }

    public void registerAdpater(TrimModelLoaderAdapter adapter, List<Item> items) {
        registerAdpater(adapter, items.stream().map(Registries.ITEM::getId).collect(Collectors.toSet()));
    }

    public void registerAdpater(TrimModelLoaderAdapter adpater, Set<Identifier> itemIds) {
        itemIds.forEach(id -> adapters.put(id, adpater));
    }

    private TrimModelLoaderAdapter getAdapter(Item item) {
        return adapters.getOrDefault(Registries.ITEM.getId(item), vanillaAdapter);
    }

    public Map<Identifier, Resource> loadModels(Map<Identifier, Resource> original) {
        Map<Identifier, Resource> loaded = new HashMap<>(original);
        List<TrimmableResource> trimmableResources = findTrimmableResources(original);
        for(TrimmableResource trimmableResource : trimmableResources) {
            TrimmableItemModel itemModel = resourceParser.fromResource(trimmableResource.resource(), TrimmableItemModel.class);
            if(itemModel == null) continue;

            Identifier modelId = trimmableResource.modelId().withSuffixedPath("_%s_trim".formatted(AllTheTrims.DYNAMIC));

            if(AllTheTrimsClient.getConfig().overrideExisting) {
                itemModel.overrides.forEach(modelOverride -> modelOverride.model = modelId.toString());
            }

            itemModel.addOverride(ModelOverride.builder()
                    .withModel(modelId.toString())
                    .withPredicate(TrimModelPredicate.of(0.6632484f))
                    .build());

            itemModel.overrides.sort(Comparator.comparing(override -> override.predicate.trimType));

            Resource newResource = resourceParser.toResource(trimmableResource.resource().getPack(), itemModel);
            loaded.put(trimmableResource.resourceId(), newResource);

            Resource overrideResource = createModelOverride(itemModel, trimmableResource);
            Identifier overrideResourceId = modelId.withPrefixedPath("models/").withSuffixedPath(".json");
            loaded.put(overrideResourceId, overrideResource);

            Debugger.createJson("resources/%s".formatted(trimmableResource.resourceId()), newResource);
            Debugger.createJson("resources/%s".formatted(overrideResourceId), overrideResource);
        }
        return loaded;
    }

    /**
     * Generates a list of for valid trimmable item
     */
    private List<TrimmableResource> findTrimmableResources(Map<Identifier, Resource> existing) {
        List<TrimmableResource> trimmableResources = new ArrayList<>();
        for (Identifier resourceId : existing.keySet()) {
            String resourcePath = resourceId.getPath();
            Matcher matcher = itemIdPattern.matcher(resourcePath);
            if (!matcher.matches()) continue;

            String itemPath = matcher.group(1);
            Identifier itemId = Identifier.of(resourceId.getNamespace(), itemPath);
            Item item = Registries.ITEM.get(itemId);
            if (item == Items.AIR) continue;
            if (!getAdapter(item).canTrim(item)) continue;

            trimmableResources.add(new TrimmableResource(item, resourceId, existing.get(resourceId)));
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

        return resourceParser.toResource(trimmableResource.resource().getPack(), itemModel);
    }
}
