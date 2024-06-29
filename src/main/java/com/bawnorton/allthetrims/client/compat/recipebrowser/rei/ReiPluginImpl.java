package com.bawnorton.allthetrims.client.compat.recipebrowser.rei;

import com.bawnorton.allthetrims.AllTheTrims;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.displays.DefaultSmithingDisplay;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmithingTrimRecipe;

@SuppressWarnings("UnstableApiUsage")
public final class ReiPluginImpl implements REIClientPlugin {
    public static final CategoryIdentifier<DefaultSmithingDisplay> TRIMMING = CategoryIdentifier.of(AllTheTrims.MOD_ID, "plugins/smithing");

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new TrimSmithingCategory());
        registry.addWorkstations(TRIMMING, EntryStacks.of((Items.SMITHING_TABLE)));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipesFiller(SmithingTrimRecipe.class, RecipeType.SMITHING, TrimSmithingDisplay::forRecipe);
    }
}
