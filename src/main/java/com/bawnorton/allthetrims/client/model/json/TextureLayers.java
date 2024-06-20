package com.bawnorton.allthetrims.client.model.json;

import java.util.List;

public final class TextureLayers {
    public List<String> layers;

    private TextureLayers(List<String> layers) {
        this.layers = layers;
    }

    public static TextureLayers of(List<String> layers) {
        return new TextureLayers(layers);
    }
}
