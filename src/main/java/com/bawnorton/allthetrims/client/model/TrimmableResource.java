package com.bawnorton.allthetrims.client.model;

import net.minecraft.data.client.ModelIds;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

public record TrimmableResource(Item item, Identifier resourceId, Resource resource) {
    public Identifier modelId() {
        return ModelIds.getItemModelId(item);
    }

    public String equipmentType() {
        if(item instanceof ElytraItem) return "elytra";

        return switch (((Equipment) item).getSlotType()) {
            case HEAD -> "helmet";
            case CHEST -> "chestplate";
            case LEGS -> "leggings";
            case FEET -> "boots";
            default -> null;
        };
    }
}
