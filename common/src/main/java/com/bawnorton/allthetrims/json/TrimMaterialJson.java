package com.bawnorton.allthetrims.json;

import com.google.gson.JsonObject;

public record TrimMaterialJson(String assetName, Description description, String ingredient, Float itemModelIndex) implements JsonRepresentable {
    public static TrimMaterialJson fromJson(JsonObject json) {
        return new TrimMaterialJson(
            JsonHelper.getString(json, "asset_name"),
            Description.fromJson(JsonHelper.getObject(json, "description")),
            JsonHelper.getString(json, "ingredient"),
            JsonHelper.getFloat(json, "item_model_index")
        );
    }

    public TrimMaterialJson(String assetName, String colour, String translate, String ingredient, Float itemModelIndex) {
        this(assetName, new Description(colour, translate), ingredient, itemModelIndex);
    }

    public JsonObject asJson() {
        JsonObject json = new JsonObject();
        json.addProperty("asset_name", assetName);
        json.add("description", description.asJson());
        json.addProperty("ingredient", ingredient);
        json.addProperty("item_model_index", itemModelIndex);
        return json;
    }

    public record Description(String colour, String translate) implements JsonRepresentable {
        public static Description fromJson(JsonObject json) {
            return new Description(
                JsonHelper.getStringOrElse(json, "color", "#FFFFFF"),
                JsonHelper.getStringOrElse(json, "translate", "")
            );
        }

        public JsonObject asJson() {
            JsonObject json = new JsonObject();
            json.addProperty("color", colour);
            json.addProperty("translate", translate);
            return json;
        }
    }
}
