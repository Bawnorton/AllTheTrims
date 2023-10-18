package com.bawnorton.allthetrims.client.implementation.jei;

import com.bawnorton.allthetrims.AllTheTrims;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.library.plugins.vanilla.crafting.VanillaRecipes;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.recipe.SmithingTrimRecipe;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class JeiPluginImpl implements IModPlugin {
    static final RecipeType<SmithingRecipe> smithingRecipeType = RecipeType.create(AllTheTrims.MOD_ID, "smithing", SmithingRecipe.class);
    static IRecipeCategory<SmithingRecipe> smithingCategory;

    public static boolean isTrimmingRecipe(SmithingRecipe smithingRecipe) {
        return smithingRecipe instanceof SmithingTrimRecipe;
    }

    @Override
    public @NotNull Identifier getPluginUid() {
        return new Identifier(AllTheTrims.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        registration.addRecipeCategories(smithingCategory = new AllTheTrimsSmithingRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        IIngredientManager ingredientManager = registration.getIngredientManager();
        VanillaRecipes vanillaRecipes = new VanillaRecipes(ingredientManager);
        registration.addRecipes(smithingRecipeType, vanillaRecipes.getSmithingRecipes(smithingCategory));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(Blocks.SMITHING_TABLE), smithingRecipeType);
    }
}
