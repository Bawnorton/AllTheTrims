package com.bawnorton.allthetrims.client.model.armour.json;

public final class ModelOverride {
    private ModelOverride(String model, TrimModelPredicate predicate) {
        this.model = model;
        this.predicate = predicate;
    }

    public String model;
    public TrimModelPredicate predicate;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String model;
        private TrimModelPredicate predicate;

        public Builder withModel(String model) {
            this.model = model;
            return this;
        }

        public Builder withPredicate(TrimModelPredicate predicate) {
            this.predicate = predicate;
            return this;
        }

        public ModelOverride build() {
            return new ModelOverride(model, predicate);
        }
    }
}
