package com.bawnorton.allthetrims.client.implementation.emi;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.recipe.special.EmiSmithingTrimRecipe;
import net.minecraft.recipe.SmithingRecipe;

public class AllTheTrimsSmithingRecipe extends EmiSmithingTrimRecipe {
    public AllTheTrimsSmithingRecipe(EmiIngredient template, EmiIngredient input, EmiIngredient addition, EmiStack output, SmithingRecipe recipe) {
        super(template, input, addition, output, recipe);
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EmiPluginImpl.TRIMMING;
    }
}
