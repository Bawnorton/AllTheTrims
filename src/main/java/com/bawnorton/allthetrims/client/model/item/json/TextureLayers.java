package com.bawnorton.allthetrims.client.model.item.json;

import java.util.ArrayList;
import java.util.List;

public final class TextureLayers {
    public List<String> layers;

    private TextureLayers(List<String> layers) {
        this.layers = layers;
    }

    public static TextureLayers empty() {
        return new TextureLayers(new ArrayList<>());
    }

    public static TextureLayers of(List<String> layers) {
        return new TextureLayers(layers);
    }
}
