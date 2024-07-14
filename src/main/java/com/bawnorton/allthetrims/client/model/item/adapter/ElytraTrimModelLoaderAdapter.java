package com.bawnorton.allthetrims.client.model.item.adapter;

import com.bawnorton.allthetrims.AllTheTrims;
import dev.kikugie.elytratrims.api.ElytraTrimsAPI;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import java.util.List;

public final class ElytraTrimModelLoaderAdapter extends TrimModelLoaderAdapter {
    public static final List<Item> APPLICABLE = Registries.ITEM.stream().filter(ElytraTrimsAPI::isProbablyElytra).toList();

    @Override
    public boolean canTrim(Item item) {
        return ElytraTrimsAPI.isProbablyElytra(item);
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
