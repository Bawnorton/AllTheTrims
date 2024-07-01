package com.bawnorton.allthetrims.client.model.armour.json;

public final class TrimModelPredicate {
    private TrimModelPredicate(float trimType) {
        this.trimType = trimType;
    }

    public float trimType;

    public static TrimModelPredicate of(float trimType) {
        return new TrimModelPredicate(trimType);
    }
}
