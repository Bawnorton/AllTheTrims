package com.bawnorton.allthetrims.util;

import net.minecraft.entity.EquipmentSlot;

public enum TrimSlot {
    BOOTS,
    LEGGINGS,
    CHESTPLATE,
    HELMET;

    public static TrimSlot fromString(String string) {
        try {
            return TrimSlot.valueOf(string.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid trim slot: " + string);
        }
    }

    public static TrimSlot fromEquipmentSlot(EquipmentSlot slot) {
        return switch (slot) {
            case FEET -> BOOTS;
            case LEGS -> LEGGINGS;
            case CHEST -> CHESTPLATE;
            case HEAD -> HELMET;
            default -> throw new IllegalArgumentException("Invalid equipment slot: " + slot);
        };
    }

    public String toString() {
        return this.name().toLowerCase();
    }
}
