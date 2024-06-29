package com.bawnorton.allthetrims.client.compat.recipebrowser.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.plugin.common.displays.DefaultSmithingDisplay;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.SmithingTrimRecipe;
import java.util.List;

public final class TrimSmithingDisplay extends DefaultSmithingDisplay {
    private TrimSmithingDisplay(DefaultSmithingDisplay display) {
        super(display.getInputEntries(), display.getOutputEntries(), display.getDisplayLocation());
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ReiPluginImpl.TRIMMING;
    }

    public static List<TrimSmithingDisplay> forRecipe(RecipeEntry<SmithingTrimRecipe> recipe) {
        return DefaultSmithingDisplay.fromTrimming(recipe).stream()
                .map(TrimSmithingDisplay::new)
                .toList();
    }
}