package com.bawnorton.allthetrims.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record TrimModelOverrideEntryJson(String model, ModelPredicate<?> predicate) implements JsonRepresentable {
    public static TrimModelOverrideEntryJson fromJson(JsonObject json) {
        return new TrimModelOverrideEntryJson(
            JsonHelper.getString(json, "model"),
            ModelPredicate.fromJson(JsonHelper.getObject(json, "predicate"))
        );
    }

    public static List<TrimModelOverrideEntryJson> fromJsonArray(JsonArray overrides) {
        return JsonHelper.parseArray(overrides, TrimModelOverrideEntryJson::fromJson);
    }

    public static JsonElement toJsonArray(List<TrimModelOverrideEntryJson> overrides) {
        return JsonHelper.serializeArray(overrides, TrimModelOverrideEntryJson::asJson);
    }

    public JsonObject asJson() {
        JsonObject json = new JsonObject();
        json.addProperty("model", model);
        json.add("predicate", predicate.asJson());
        return json;
    }

    public @Nullable String assetName() {
        Identifier modelId = new Identifier(model);
        String path = modelId.getPath();
        int armourTypeIndex = path.indexOf("chestplate");
        if (armourTypeIndex == -1) return null;

        String afterArmourType = path.substring(armourTypeIndex + "chestplate".length());
        int trimIndex = afterArmourType.indexOf("_trim");
        if (trimIndex == -1) return null;

        return afterArmourType.substring(1, trimIndex);
    }

    public interface ModelPredicate<T> extends JsonRepresentable {
        static ModelPredicate<?> fromJson(JsonObject json) {
            if(!json.has("trim_type")) throw new IllegalArgumentException("Predicate does not contain \"trim_type\": " + json);
            JsonElement element = json.get("trim_type");
            if (!(element instanceof JsonPrimitive primitive)) {
                throw new IllegalArgumentException("\"trim_type\" is not primitive: " + json);
            }
            if (primitive.isNumber()) return new FloatModelPredicate(primitive.getAsFloat());
            return new StringModelPredicate(primitive.getAsString());
        }

        T trimType();

        default StringModelPredicate asStringPredicate() {
            return new StringModelPredicate(String.valueOf(trimType()));
        }

        default FloatModelPredicate asFloatPredicate() {
            return new FloatModelPredicate(Float.parseFloat(asStringPredicate().trimType()));
        }

        default boolean isStringPredicate() {
            return this instanceof StringModelPredicate;
        }

        default boolean isFloatPredicate() {
            return this instanceof FloatModelPredicate;
        }
    }

    public record StringModelPredicate(String trimType) implements ModelPredicate<String>{
        @Override
        public JsonObject asJson() {
            JsonObject json = new JsonObject();
            json.addProperty("trim_type", trimType);
            return json;
        }
    }

    public record FloatModelPredicate(Float trimType) implements ModelPredicate<Float> {
        @Override
        public JsonObject asJson() {
            JsonObject json = new JsonObject();
            json.addProperty("trim_type", trimType);
            return json;
        }
    }
}
