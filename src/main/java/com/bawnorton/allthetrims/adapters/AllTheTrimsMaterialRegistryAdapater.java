package com.bawnorton.allthetrims.adapters;

import com.bawnorton.runtimetrims.registry.adapter.TrimMaterialRegistryAdapter;
import net.minecraft.item.Item;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class AllTheTrimsMaterialRegistryAdapater extends TrimMaterialRegistryAdapter {
    @Override
    public Map<Identifier, RegistryEntry<Item>> getNewMaterials(SimpleRegistry<ArmorTrimMaterial> trimMaterialRegistry) {
        Set<Item> includedMaterials = trimMaterialRegistry.stream()
                .map(trimMaterial -> trimMaterial.ingredient().value())
                .collect(Collectors.toSet());

        return Registries.ITEM.stream()
                .filter(item -> !includedMaterials.contains(item))
                .map(item -> new Pair<>(Registries.ITEM.getId(item), Registries.ITEM.getEntry(item)))
                .collect(HashMap::new, (map, pair) -> map.put(pair.getLeft(), pair.getRight()), HashMap::putAll);
    }
}
