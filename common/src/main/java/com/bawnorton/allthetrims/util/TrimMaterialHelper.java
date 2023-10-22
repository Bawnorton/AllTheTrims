package com.bawnorton.allthetrims.util;

import com.bawnorton.allthetrims.json.TrimMaterialJson;
import com.bawnorton.allthetrims.json.TrimModelOverrideEntryJson;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class TrimMaterialHelper {
    public static final Set<TrimMaterialJson> BUILTIN_TRIM_MATERIALS = new HashSet<>();
    public static final Set<TrimModelOverrideEntryJson> BUILTIN_TRIM_MODEL_OVERRIDES = new HashSet<>();

    public static void forEachTrimMaterial(BiConsumer<Item, Boolean> consumer) {
        for (Item item : Registries.ITEM) {
            consumer.accept(item, isBuiltinMaterial(item));
        }
    }

    public static void forEachBuiltinTrimMaterial(Consumer<TrimMaterialJson> consumer) {
        for (TrimMaterialJson trimMaterial : BUILTIN_TRIM_MATERIALS) {
            consumer.accept(trimMaterial);
        }
    }

    public static void forEachBuiltinTrimModelOverride(Consumer<TrimModelOverrideEntryJson> consumer) {
        for (TrimModelOverrideEntryJson trimModelOverride: BUILTIN_TRIM_MODEL_OVERRIDES) {
            consumer.accept(trimModelOverride);
        }
    }

    public static boolean isBuiltinMaterial(Item item) {
        return BUILTIN_TRIM_MATERIALS.stream().anyMatch((material) -> material.ingredient().equals(Registries.ITEM.getId(item).toString()));
    }
}
