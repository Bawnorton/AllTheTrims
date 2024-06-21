package com.bawnorton.allthetrims.client.model.adapter;

import net.minecraft.item.Item;

public abstract class TrimModelLoaderAdapter {
    public abstract boolean canTrim(Item item);

    public abstract Integer getLayerCount(Item item);

    public abstract String getLayerName(Item item, int layerIndex);
}
