package com.bawnorton.allthetrims.client.model.item.json;

import com.google.gson.annotations.SerializedName;

public final class TrimModelPredicate {
    private TrimModelPredicate(float trimType) {
        this.trimType = trimType;
    }

    @SerializedName(value = "trim_type", alternate = "minecraft:trim_type")
    public float trimType;

    public static TrimModelPredicate of(float trimType) {
        return new TrimModelPredicate(trimType);
    }
}
