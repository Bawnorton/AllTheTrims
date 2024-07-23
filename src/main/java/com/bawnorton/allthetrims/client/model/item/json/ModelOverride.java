package com.bawnorton.allthetrims.client.model.item.json;

import com.google.gson.JsonObject;

public final class ModelOverride {
    private ModelOverride(String model, JsonObject predicate) {
        this.model = model;
        this.predicate = predicate;
    }

    public String model;
    public JsonObject predicate;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String model;
        private JsonObject predicate;

        public Builder withModel(String model) {
            this.model = model;
            return this;
        }

        public Builder withPredicate(JsonObject predicate) {
            this.predicate = predicate;
            return this;
        }

        public ModelOverride build() {
            return new ModelOverride(model, predicate);
        }
    }
}
