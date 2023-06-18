package com.bawnorton.allthetrims.mixin;

import com.bawnorton.allthetrims.AllTheTrims;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.*;

@Mixin(SimpleRegistry.class)
public abstract class SimpleRegistryMixin {
    @SuppressWarnings("unchecked")
    @ModifyVariable(method = "populateTags", at = @At("LOAD"), index = 1, argsOnly = true)
    private <T> Map<TagKey<T>, List<RegistryEntry<T>>> addAllItemsToTrimMaterialTag(Map<TagKey<T>, List<RegistryEntry<T>>> tagEntries) {
        if (tagEntries.containsKey(ItemTags.TRIM_MATERIALS)) {
            tagEntries = new HashMap<>(tagEntries);
            List<RegistryEntry<T>> entries = new ArrayList<>(tagEntries.get(ItemTags.TRIM_MATERIALS));
            if(AllTheTrims.USED_MATERIALS.isEmpty()) {
                for(RegistryEntry<T> entry : entries) {
                    if(entry.value() instanceof Item item) {
                        AllTheTrims.addUsedAsMaterial(item);
                    }
                }
            }
            entries.addAll(Registries.ITEM.stream().map(item -> (RegistryEntry<T>) Registries.ITEM.getEntry(item)).toList());
            tagEntries.put((TagKey<T>) ItemTags.TRIM_MATERIALS, entries);
            tagEntries = Collections.unmodifiableMap(tagEntries);
        }
        if (tagEntries.containsKey(ItemTags.TRIMMABLE_ARMOR)) {
            tagEntries = new HashMap<>(tagEntries);
            List<RegistryEntry<T>> entries = new ArrayList<>(tagEntries.get(ItemTags.TRIMMABLE_ARMOR));
            if(AllTheTrims.USED_ARMOUR.isEmpty()) {
                for(RegistryEntry<T> entry : entries) {
                    if(entry.value() instanceof Item item) {
                        AllTheTrims.addUsedAsEquipment(item);
                    }
                }
            }
            entries.addAll(Registries.ITEM.stream().filter(item -> item instanceof Equipment).map(item -> (RegistryEntry<T>) Registries.ITEM.getEntry(item)).toList());
            tagEntries.put((TagKey<T>) ItemTags.TRIMMABLE_ARMOR, entries);
            tagEntries = Collections.unmodifiableMap(tagEntries);
        }
        return tagEntries;
    }
}
