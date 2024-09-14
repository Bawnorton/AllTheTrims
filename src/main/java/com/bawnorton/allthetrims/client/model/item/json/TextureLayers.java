package com.bawnorton.allthetrims.client.model.item.json;

import java.util.HashMap;
import java.util.Map;

public final class TextureLayers {
    public Map<String, String> layers;

    private TextureLayers(Map<String, String> layers) {
        this.layers = layers;
    }

    public static TextureLayers empty() {
        return new TextureLayers(new HashMap<>());
    }

    public static TextureLayers of(Map<String, String> layers) {
        return new TextureLayers(layers);
    }
}
