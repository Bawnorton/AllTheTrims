package com.bawnorton.allthetrims.json;

import com.google.gson.*;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class JsonHelper {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
                                                      .setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                                                      .create();

    public static <T> T fromJsonReader(BufferedReader reader, Class<T> clazz) {
        return GSON.fromJson(reader, clazz);
    }

    public static <T> T fromJsonString(String string, Class<T> jsonObjectClass) {
        return GSON.fromJson(string, jsonObjectClass);
    }

    public static String toJsonString(JsonElement jsonElement) {
        return GSON.toJson(jsonElement);
    }

    public static String toJsonString(JsonRepresentable jsonRepresentable) {
        return toJsonString(jsonRepresentable.asJson());
    }

    public static String getString(JsonObject json, String key) {
        return json.get(key).getAsString();
    }

    public static String getStringOrElse(JsonObject json, String key, String defaultValue) {
        JsonElement element = json.get(key);
        if (element == null) return defaultValue;
        return element.getAsString();
    }

    public static JsonObject getObject(JsonObject json, String key) {
        return json.get(key).getAsJsonObject();
    }

    public static Float getFloat(JsonObject json, String key) {
        return json.get(key).getAsFloat();
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> asStringMap(JsonObject json) {
        return GSON.fromJson(json, Map.class);
    }

    public static <T> List<T> parseArray(JsonArray array, Function<JsonObject, T> parser) {
        List<T> list = new ArrayList<>();
        for (JsonElement element : array) {
            if (!element.isJsonObject()) continue;
            try {
                list.add(parser.apply(element.getAsJsonObject()));
            } catch (RuntimeException ignored) {
            }
        }
        return list;
    }

    public static <T> JsonArray serializeArray(List<T> list, Function<T, JsonObject> serializer) {
        JsonArray array = new JsonArray();
        for (T element : list) {
            array.add(serializer.apply(element));
        }
        return array;

    }
}
