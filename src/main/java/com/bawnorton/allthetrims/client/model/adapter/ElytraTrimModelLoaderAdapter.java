package com.bawnorton.allthetrims.client.model.adapter;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.client.compat.Compat;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import java.util.List;

public final class ElytraTrimModelLoaderAdapter extends TrimModelLoaderAdapter {
    public static final List<Item> APPLICABLE = Registries.ITEM.stream().filter(item -> item instanceof ElytraItem).toList();

    @Override
    public boolean canTrim(Item item) {
        if(Compat.getElytraTrimsCompat().isEmpty()) return false;

        return item instanceof ElytraItem;
    }

    @Override
    public Integer getLayerCount(Item item) {
        return 6;
    }

    @Override
    public String getLayerName(Item item, int layerIndex) {
        return "minecraft:trims/items/elytra/default_%s_%s".formatted(layerIndex, AllTheTrims.DYNAMIC);
    }
}
