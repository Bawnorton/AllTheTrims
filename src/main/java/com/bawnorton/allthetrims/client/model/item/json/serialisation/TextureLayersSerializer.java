package com.bawnorton.allthetrims.client.model.item.json.serialisation;

import com.bawnorton.allthetrims.client.model.item.json.TextureLayers;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TextureLayersSerializer implements JsonSerializer<TextureLayers>, JsonDeserializer<TextureLayers> {

    @Override
    public TextureLayers deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Map<String, String> layers = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            layers.put(entry.getKey(), entry.getValue().getAsString());
        }
        return TextureLayers.of(layers);
    }

    @Override
    public JsonElement serialize(TextureLayers src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        src.layers.forEach((key, value) -> jsonObject.add(key, context.serialize(value)));
        return jsonObject;
    }
}
