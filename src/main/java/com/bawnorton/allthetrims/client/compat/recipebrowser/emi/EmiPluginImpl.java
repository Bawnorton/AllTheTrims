package com.bawnorton.allthetrims.client.compat.recipebrowser.emi;

import com.bawnorton.allthetrims.AllTheTrims;
import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiRecipeSorting;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.mixin.accessor.SmithingTrimRecipeAccessor;
import dev.emi.emi.recipe.special.EmiSmithingTrimRecipe;
import dev.emi.emi.runtime.EmiDrawContext;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;

public final class EmiPluginImpl implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        EmiRecipeCategory trimming = new EmiRecipeCategory(
                AllTheTrims.id("trimming"),
                new CyclingEmiStack(
                        Registries.ITEM.streamEntries()
                                .filter(ref -> ref.isIn(ItemTags.TRIM_TEMPLATES))
                                .map(RegistryEntry.Reference::value)
                                .map(EmiStack::of)
                                .toList(),
                    1000
                ),
                (raw, x, y, delta) -> EmiDrawContext.wrap(raw).drawTexture(EmiRenderHelper.WIDGETS, x, y, 240, 224, 16, 16),
                EmiRecipeSorting.compareInputThenOutput()
        );

        registry.addCategory(trimming);
        registry.addWorkstation(trimming, EmiStack.of(Items.SMITHING_TABLE));

        for (RecipeEntry<SmithingRecipe> recipeEntry : registry.getRecipeManager().listAllOfType(RecipeType.SMITHING)) {
            SmithingRecipe recipe = recipeEntry.value();
            if (recipe instanceof SmithingTrimRecipeAccessor accessor) {
                registry.addRecipe(new EmiSmithingTrimRecipe(
                        EmiIngredient.of(accessor.getTemplate()),
                        EmiIngredient.of(accessor.getBase()),
                        EmiIngredient.of(accessor.getAddition()),
                        EmiStack.of(EmiPort.getOutput(recipe)),
                        recipe
                ){
                    @Override
                    public EmiRecipeCategory getCategory() {
                        return trimming;
                    }
                });
            }
        }
    }
}