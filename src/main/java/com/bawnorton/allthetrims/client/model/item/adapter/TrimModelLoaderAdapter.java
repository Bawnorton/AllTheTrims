package com.bawnorton.allthetrims.client.model.item.adapter;

import net.minecraft.item.Item;

public abstract class TrimModelLoaderAdapter {
    /**
     * Whether dynamic trims should be generated for the item.<br>
     * All items are passed here so extending {@link DefaultTrimModelLoaderAdapter} may be desired for the existing "is armour" checks.
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
