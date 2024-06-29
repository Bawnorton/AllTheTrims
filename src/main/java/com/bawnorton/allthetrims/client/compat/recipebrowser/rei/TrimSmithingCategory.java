package com.bawnorton.allthetrims.client.compat.recipebrowser.rei;

import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.client.categories.DefaultSmithingCategory;
import me.shedaniel.rei.plugin.common.displays.DefaultSmithingDisplay;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;

public final class TrimSmithingCategory extends DefaultSmithingCategory {
    @Override
    public CategoryIdentifier<? extends DefaultSmithingDisplay> getCategoryIdentifier() {
        return ReiPluginImpl.TRIMMING;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("emi.category.allthetrims.trimming");
    }

    @Override
    public Renderer getIcon() {
        return new CyclingEntryStack<>(
                Registries.ITEM.streamEntries()
                        .filter(ref -> ref.isIn(ItemTags.TRIM_TEMPLATES))
                        .map(RegistryEntry.Reference::value)
                        .map(EntryStacks::of)
                        .toList(),
                1000
        );
    }
}