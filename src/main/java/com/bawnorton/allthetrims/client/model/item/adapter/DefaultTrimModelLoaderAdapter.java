package com.bawnorton.allthetrims.client.model.item.adapter;

import com.bawnorton.allthetrims.AllTheTrims;
import net.minecraft.item.AnimalArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;

public class DefaultTrimModelLoaderAdapter extends TrimModelLoaderAdapter {
    @Override
    public boolean canTrim(Item item) {
        if (item instanceof AnimalArmorItem) return false;
        if (!(item instanceof Equipment equipment)) return false;
        if (item instanceof ElytraItem) return false; // If ET is present, ElytraTrimModelLoaderAdapater takes care of elytras

        return equipment.getSlotType().isArmorSlot();
    }

    @Override
    public Integer getLayerCount(Item item) {
        return 4;
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
