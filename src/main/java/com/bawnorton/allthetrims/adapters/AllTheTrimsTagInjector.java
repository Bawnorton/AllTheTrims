package com.bawnorton.allthetrims.adapters;

import com.bawnorton.allthetrims.client.compat.Compat;
import com.bawnorton.runtimetrims.tag.adapter.TagInjectionAdapter;
import net.minecraft.item.AnimalArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import java.util.Set;
import java.util.stream.Collectors;

public final class AllTheTrimsTagInjector extends TagInjectionAdapter {
    @Override
    public Set<Item> getTrimmableArmour() {
        return Registries.ITEM.stream()
                .filter(item -> !(item instanceof AnimalArmorItem))
                .filter(item -> !(item instanceof ElytraItem) || Compat.getElytraTrimsCompat().isPresent())
                .filter(item -> item instanceof Equipment equipment && equipment.getSlotType().isArmorSlot())
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Item> getTrimMaterials() {
        return Registries.ITEM.stream().filter(item -> item != Items.AIR).collect(Collectors.toSet());
    }
}
