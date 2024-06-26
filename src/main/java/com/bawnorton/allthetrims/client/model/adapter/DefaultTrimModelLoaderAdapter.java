package com.bawnorton.allthetrims.client.model.adapter;

import com.bawnorton.allthetrims.AllTheTrims;
import net.minecraft.item.AnimalArmorItem;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

public final class DefaultTrimModelLoaderAdapter extends TrimModelLoaderAdapter {
    @Override
    public boolean canTrim(Item item) {
        if (item instanceof AnimalArmorItem) return false;
        if (!(item instanceof Equipment equipment)) return false;

        return equipment.getSlotType().isArmorSlot();
    }

    @Override
    public Integer getLayerCount(Item item) {
        if(Registries.ITEM.getId(item).getNamespace().equals("minecraft")) {
            return 4;
        }
        return 8;
    }

    @Override
    public String getLayerName(Item item, int layerIndex) {
        return "minecraft:trims/items/%s_trim_%d_%s".formatted(
                getEquipmentType((Equipment) item),
                layerIndex,
                AllTheTrims.DYNAMIC
        );
    }

    private String getEquipmentType(Equipment equipment) {
        return switch (equipment.getSlotType()) {
            case HEAD -> "helmet";
            case CHEST -> "chestplate";
            case LEGS -> "leggings";
            case FEET -> "boots";
            default -> null;
        };
    }
}
