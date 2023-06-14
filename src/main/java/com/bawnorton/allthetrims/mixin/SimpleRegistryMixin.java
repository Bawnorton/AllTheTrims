package com.bawnorton.allthetrims.mixin;

import net.minecraft.item.ArmorItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(SimpleRegistry.class)
public abstract class SimpleRegistryMixin {
    @SuppressWarnings("unchecked")
    @ModifyVariable(method = "populateTags", at = @At("LOAD"), index = 1, argsOnly = true)
    private <T> Map<TagKey<T>, List<RegistryEntry<T>>> addAllItemsToTrimMaterialTag(Map<TagKey<T>, List<RegistryEntry<T>>> tagEntries) {
        if(tagEntries.containsKey(ItemTags.TRIM_MATERIALS)) {
            tagEntries = new HashMap<>(tagEntries);
            tagEntries.put((TagKey<T>) ItemTags.TRIM_MATERIALS, Registries.ITEM.stream().map(item -> (RegistryEntry<T>) Registries.ITEM.getEntry(item)).toList());
            tagEntries = Collections.unmodifiableMap(tagEntries);
        }
        if (tagEntries.containsKey(ItemTags.TRIMMABLE_ARMOR)) {
            tagEntries = new HashMap<>(tagEntries);
            tagEntries.put((TagKey<T>) ItemTags.TRIMMABLE_ARMOR, Registries.ITEM.stream().filter(item -> item instanceof ArmorItem).map(item -> (RegistryEntry<T>) Registries.ITEM.getEntry(item)).toList());
            tagEntries = Collections.unmodifiableMap(tagEntries);
            System.out.println(tagEntries);
        }
        return tagEntries;
    }
}
