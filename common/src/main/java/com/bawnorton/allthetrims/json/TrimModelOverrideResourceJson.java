package com.bawnorton.allthetrims.json;

import com.google.gson.JsonObject;

import java.util.Map;

public record TrimModelOverrideResourceJson(String parent, Textures textures, String assetName) implements JsonRepresentable {
    public static TrimModelOverrideResourceJson fromJson(JsonObject json, String assetName) {
        return new TrimModelOverrideResourceJson(
            JsonHelper.getString(json, "parent"),
            Textures.fromJson(JsonHelper.getObject(json, "textures")),
            assetName
        );
    }

    public JsonObject asJson() {
        JsonObject json = new JsonObject();
        json.addProperty("parent", parent);
        json.add("textures", textures.asJson());
        return json;
    }

    private record Textures(Map<String, String> layers) implements JsonRepresentable {
        public static Textures fromJson(JsonObject json) {
            return new Textures(JsonHelper.asStringMap(json));
        }

        public JsonObject asJson() {
            JsonObject json = new JsonObject();
            layers.forEach(json::addProperty);
            return json;
        }
    }
}
