package com.bawnorton.allthetrims.util;

import com.bawnorton.allthetrims.AllTheTrims;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

import java.util.function.BiConsumer;

public abstract class TrimIndexHelper {
    public static void loopTrimMaterials(BiConsumer<Item, Double> biConsumer) {
        double max = getMaxTrimMaterialIndex();
        double index = 1f / max;
        for (Item item : Registries.ITEM) {
            if (AllTheTrims.isUsedAsMaterial(item)) continue;
            biConsumer.accept(item, index);
            index += 1f / max;
            if (isRoundedTo3dp(index)) {
                index += 1f / max;
                max += 1;
            }
            if (index > 1) {
                AllTheTrims.LOGGER.error("Ran out of trim material indexes! Some items may not be usable as trim materials!");
                break;
            }
        }
    }

    public static double getMaxTrimMaterialIndex() {
        return getNextPrime((Registries.ITEM.getIds().size() + 10) * 10); // reduce chance of collision with other mods
    }

    private static boolean isRoundedTo3dp(double d) {
        return Math.round(d * 1000) % 1000 == 0;
    }

    private static double getNextPrime(double n) {
        double intNum = Math.floor(n) + 1;
        while (!isPrime(intNum)) intNum += 1;
        return intNum;
    }

    private static boolean isPrime(double n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        int i = 5;
        while (i * i <= n) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
            i += 6;
        }
        return true;
    }
}
