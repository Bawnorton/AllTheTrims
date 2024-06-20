package com.bawnorton.allthetrims.client.model;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.client.debug.Debugger;
import com.bawnorton.allthetrims.client.model.json.ModelOverride;
import com.bawnorton.allthetrims.client.model.json.TextureLayers;
import com.bawnorton.allthetrims.client.model.json.TrimModelPredicate;
import com.bawnorton.allthetrims.client.model.json.TrimmableItemModel;
import net.minecraft.item.AnimalArmorItem;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DynamicTrimModelLoader {
    private static final Pattern itemIdPattern = Pattern.compile("^models/item/(.+)?(?=.json).json$");
    private final ResourceParser resourceParser = new ResourceParser();

    public Map<Identifier, Resource> loadModels(Map<Identifier, Resource> original) {
        Map<Identifier, Resource> loaded = new HashMap<>(original);
        List<EquipmentResource> equipment = findTrimmableEquipment(original);
        for(EquipmentResource equipmentResource : equipment) {
            TrimmableItemModel itemModel = resourceParser.fromResource(equipmentResource.resource(), TrimmableItemModel.class);
            if(itemModel == null) continue;

            Identifier modelId = equipmentResource.modelId().withSuffixedPath("_dynamic_trim");

            if(AllTheTrims.getConfig().overrideExisting) {
                itemModel.overrides.forEach(modelOverride -> modelOverride.model = modelId.toString());
            }

            itemModel.addOverride(ModelOverride.builder()
                    .withModel(modelId.toString())
                    .withPredicate(TrimModelPredicate.of(0.6632484f))
                    .build());


            Resource newResource = resourceParser.toResource(equipmentResource.resource().getPack(), itemModel);
            loaded.put(equipmentResource.resourceId(), newResource);

            Resource overrideResource = createModelOverride(itemModel, equipmentResource);
            Identifier overrideResourceId = modelId.withPrefixedPath("models/").withSuffixedPath(".json");
            loaded.put(overrideResourceId, overrideResource);

            Debugger.createJson("resources/%s".formatted(equipmentResource.resourceId()), newResource);
            Debugger.createJson("resources/%s".formatted(overrideResourceId), overrideResource);
        }
        return loaded;
    }

    /**
     * Generates a list of for valid trimmable equipment
     */
    private List<EquipmentResource> findTrimmableEquipment(Map<Identifier, Resource> existing) {
        List<EquipmentResource> equipmentLookup = new ArrayList<>();
        for (Identifier resourceId : existing.keySet()) {
            String resourcePath = resourceId.getPath();
            Matcher matcher = itemIdPattern.matcher(resourcePath);
            if (!matcher.matches()) continue;

            String itemPath = matcher.group(1);
            Identifier itemId = Identifier.of(resourceId.getNamespace(), itemPath);
            Item item = Registries.ITEM.get(itemId);
            if (item == Items.AIR) continue;
            if (!(item instanceof Equipment equipment)) continue;
            if (!equipment.getSlotType().isArmorSlot()) continue;
            if (item instanceof AnimalArmorItem) continue;

            equipmentLookup.add(new EquipmentResource(equipment, resourceId, existing.get(resourceId)));
        }
        return equipmentLookup;
    }

    private Resource createModelOverride(TrimmableItemModel overriden, EquipmentResource equipmentResource) {
        List<String> layers = new ArrayList<>(overriden.textures.layers);

        for(int i = 0; i < 4; i++) {
            layers.add("minecraft:trims/items/%s_trim_%d_dynamic".formatted(
                    equipmentResource.equipmentType(),
                    i
            ));
        }
        TrimmableItemModel itemModel = TrimmableItemModel.builder()
                .parent(overriden.parent)
                .textures(TextureLayers.of(layers))
                .build();

        return resourceParser.toResource(equipmentResource.resource().getPack(), itemModel);
    }
}
