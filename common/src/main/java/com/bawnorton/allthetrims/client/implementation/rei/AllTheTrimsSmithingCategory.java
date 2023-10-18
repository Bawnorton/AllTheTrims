package com.bawnorton.allthetrims.client.implementation.rei;

import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.client.categories.DefaultSmithingCategory;
import me.shedaniel.rei.plugin.common.displays.DefaultSmithingDisplay;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class AllTheTrimsSmithingCategory extends DefaultSmithingCategory {
    @Override
    public CategoryIdentifier<? extends DefaultSmithingDisplay> getCategoryIdentifier() {
        return ReiPluginImpl.TRIMMING;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("emi.category.allthetrims.smithing");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE);
    }
}