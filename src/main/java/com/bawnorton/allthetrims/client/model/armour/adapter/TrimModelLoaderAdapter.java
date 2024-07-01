package com.bawnorton.allthetrims.client.model.armour.adapter;

import net.minecraft.item.Item;

public abstract class TrimModelLoaderAdapter {
    /**
     * Whether dynamic trims should be generated for the item.
     */
    public abstract boolean canTrim(Item item);

    /**
     * How many colours does the blank trim texture for the item use.
     */
    public abstract Integer getLayerCount(Item item);

    /**
     * Where to find the <b>dynamic</b> trim texture for a given item and index.
     */
    public abstract String getLayerName(Item item, int layerIndex);
}
