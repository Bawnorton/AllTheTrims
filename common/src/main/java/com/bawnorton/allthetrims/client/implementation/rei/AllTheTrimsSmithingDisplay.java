package com.bawnorton.allthetrims.client.implementation.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.plugin.common.displays.DefaultSmithingDisplay;
import net.minecraft.recipe.SmithingTrimRecipe;

@SuppressWarnings("UnstableApiUsage")
public class AllTheTrimsSmithingDisplay extends DefaultSmithingDisplay {
    public AllTheTrimsSmithingDisplay(SmithingTrimRecipe recipe) {
        super(recipe);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ReiPluginImpl.TRIMMING;
    }
}
