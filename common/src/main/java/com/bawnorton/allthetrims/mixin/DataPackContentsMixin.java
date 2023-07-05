package com.bawnorton.allthetrims.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.item.Equipment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.DataPackContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.*;

@Mixin(DataPackContents.class)
public abstract class DataPackContentsMixin {
    @SuppressWarnings("unchecked")
    @ModifyExpressionValue(method = "repopulateTags", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;collect(Ljava/util/stream/Collector;)Ljava/lang/Object;"))
    private static <T> Object addAllItemsToTrimmableMaterialsAndAllEquipmentToTrimmableArmourTags(Object tagEntriesObj) {
        Map<TagKey<T>, List<RegistryEntry<T>>> tagEntries = (Map<TagKey<T>, List<RegistryEntry<T>>>) tagEntriesObj;
        if (tagEntries.containsKey(ItemTags.TRIM_MATERIALS)) {
            tagEntries = new HashMap<>(tagEntries);
            List<RegistryEntry<T>> entries = new ArrayList<>(tagEntries.get(ItemTags.TRIM_MATERIALS));
            entries.addAll(Registries.ITEM.stream().map(item -> (RegistryEntry<T>) Registries.ITEM.getEntry(item)).toList());
            tagEntries.put((TagKey<T>) ItemTags.TRIM_MATERIALS, entries);
            tagEntries = Collections.unmodifiableMap(tagEntries);
        }
        if (tagEntries.containsKey(ItemTags.TRIMMABLE_ARMOR)) {
            tagEntries = new HashMap<>(tagEntries);
            List<RegistryEntry<T>> entries = new ArrayList<>(tagEntries.get(ItemTags.TRIMMABLE_ARMOR));
            entries.addAll(Registries.ITEM.stream().filter(item -> item instanceof Equipment equipment && equipment.getSlotType().isArmorSlot()).map(item -> (RegistryEntry<T>) Registries.ITEM.getEntry(item)).toList());
            tagEntries.put((TagKey<T>) ItemTags.TRIMMABLE_ARMOR, entries);
            tagEntries = Collections.unmodifiableMap(tagEntries);
        }
        return tagEntries;
    }
}
