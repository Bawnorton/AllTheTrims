package com.bawnorton.allthetrims.client.render;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import java.util.HashMap;
import java.util.Map;

public final class LayerData {
    private final Map<Identifier, Integer> maxSupportedLayers = new HashMap<>();
    private final Map<Item, Integer> trimStartLayers = new HashMap<>();

    public void setMaxSupportedLayer(Identifier trimPattern, int layer) {
        int existingLayer = maxSupportedLayers.getOrDefault(trimPattern, -1);
        if (layer > existingLayer) {
            maxSupportedLayers.put(trimPattern, layer);
        }
    }

    public void setTrimStartLayer(Item item, int layer) {
        trimStartLayers.put(item, layer);
    }

    public int getMaxSupportedLayer(Identifier trimPattern) {
        return maxSupportedLayers.getOrDefault(trimPattern, -1) + 1;
    }

    public int getTrimStartLayer(Item item) {
        return trimStartLayers.getOrDefault(item, -1);
    }
}
