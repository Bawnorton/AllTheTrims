package com.bawnorton.allthetrims.client.model;

import net.minecraft.data.client.ModelIds;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

public record EquipmentResource(Equipment equipment, Identifier resourceId, Resource resource) {
    public Identifier modelId() {
        return ModelIds.getItemModelId((Item) equipment);
    }

    public String equipmentType() {
        return switch (equipment.getSlotType()) {
            case HEAD -> "helmet";
            case CHEST -> "chestplate";
            case LEGS -> "leggings";
            case FEET -> "boots";
            default -> null;
        };
    }
}
