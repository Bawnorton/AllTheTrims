package com.bawnorton.allthetrims.util;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.Validate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class Adaptable<T> {
    private final Map<Identifier, T> adapters = new HashMap<>();
    private T defaultAdapter;

    public void setDefaultAdapter(T adapter) {
        this.defaultAdapter = adapter;
    }

    public void registerAdapter(T adapter, List<Item> items) {
        registerAdapter(adapter, items.stream().map(Registries.ITEM::getId).collect(Collectors.toSet()));
    }

    public void registerAdapter(T adpater, Set<Identifier> itemIds) {
        itemIds.forEach(id -> registerAdapter(adpater, id));
    }

    public void registerAdapter(T adapter, Identifier itemId) {
        Validate.notNull(itemId, "itemId cannot be null");
        Validate.notNull(adapter, "adapter cannot be null");
        if(adapters.containsKey(itemId)) {
            throw new IllegalArgumentException("Adapter: \"%s\" for item \"%s\" already registered".formatted(adapters.get(itemId).getClass().getSimpleName(), itemId));
        }
        adapters.put(itemId, adapter);
    }

    protected boolean hasAdapter(Identifier id) {
        return adapters.containsKey(id);
    }

    protected boolean hasAdapter(Item item) {
        return hasAdapter(Registries.ITEM.getId(item));
    }

    protected T getAdapter(Identifier id) {
        Validate.notNull(id, "id cannot be null");
        return adapters.getOrDefault(id, Validate.notNull(defaultAdapter, "Adapter for \"%s\" is not registered and no default adapter set".formatted(id)));
    }

    protected T getAdapter(Item item) {
        return getAdapter(Registries.ITEM.getId(item));
    }

    protected List<T> getAdapters() {
        List<T> adapters = new ArrayList<>(this.adapters.values());
        adapters.add(defaultAdapter);
        return adapters;
    }
}
