package com.bawnorton.allthetrims.util;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

import java.util.function.Consumer;

public abstract class TrimMaterialHelper {
    public static void loopTrimMaterials(Consumer<Item> consumer) {
        for (Item item : Registries.ITEM) {
            consumer.accept(item);
        }
    }
}
