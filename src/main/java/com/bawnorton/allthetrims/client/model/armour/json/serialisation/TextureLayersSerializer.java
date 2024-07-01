package com.bawnorton.allthetrims.client.model.armour.json.serialisation;

import com.bawnorton.allthetrims.client.model.armour.json.TextureLayers;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class TextureLayersSerializer implements JsonSerializer<TextureLayers>, JsonDeserializer<TextureLayers> {

    @Override
    public TextureLayers deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        List<String> layers = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            if (entry.getKey().startsWith("layer")) {
                layers.add(entry.getValue().getAsString());
            }
        }
        return TextureLayers.of(layers);
    }

    @Override
    public JsonElement serialize(TextureLayers src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        for (int i = 0; i < src.layers.size(); i++) {
            jsonObject.addProperty("layer" + i, src.layers.get(i));
        }
        return jsonObject;
    }
}
