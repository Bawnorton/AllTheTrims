package com.bawnorton.allthetrims.client.compat.recipebrowser.jei;

import com.bawnorton.allthetrims.AllTheTrims;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.library.plugins.vanilla.anvil.SmithingRecipeCategory;
import mezz.jei.library.plugins.vanilla.crafting.VanillaRecipes;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.recipe.SmithingTrimRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

//? if >1.20.6
import net.minecraft.recipe.RecipeEntry;
//? if neoforge
/*@mezz.jei.api.JeiPlugin*/
public final class JeiPluginImpl implements IModPlugin {
    //? if >1.20.6 {
    @SuppressWarnings("unchecked")
    public static final RecipeType<RecipeEntry<SmithingRecipe>> TRIMMING = new RecipeType<>(AllTheTrims.id("trimming"), (Class<RecipeEntry<SmithingRecipe>>) (Object) RecipeEntry.class);
    public static IRecipeCategory<RecipeEntry<SmithingRecipe>> smithingCategory;
    //?} else {
    /*public static final RecipeType<SmithingRecipe> TRIMMING = new RecipeType<>(AllTheTrims.id("trimming"), SmithingRecipe.class);
    public static IRecipeCategory<SmithingRecipe> smithingCategory;
    *///?}

    public static boolean isTrimming(SmithingRecipe recipe) {
        return recipe instanceof SmithingTrimRecipe;
    }

    @Override
    public @NotNull Identifier getPluginUid() {
        return AllTheTrims.id("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(smithingCategory = new AllTheTrimsSmithingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(TRIMMING, new VanillaRecipes(registration.getIngredientManager()).getSmithingRecipes(smithingCategory));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(Blocks.SMITHING_TABLE), TRIMMING);
    }

    private static final class AllTheTrimsSmithingRecipeCategory extends SmithingRecipeCategory {
        private final IDrawable icon;

        public AllTheTrimsSmithingRecipeCategory(IGuiHelper guiHelper) {
            super(guiHelper);
            icon = new CyclingDrawable(
                    Registries.ITEM.streamEntries()
                            .filter(ref -> ref.isIn(ItemTags.TRIM_TEMPLATES))
                            .map(RegistryEntry.Reference::value)
                            .map(Item::getDefaultStack)
                            .map(guiHelper::createDrawableItemStack)
                            .toList(),
                    1000
            );
        }

        //? if >1.20.6 {
        @Override
        public @NotNull RecipeType<RecipeEntry<SmithingRecipe>> getRecipeType() {
            return JeiPluginImpl.TRIMMING;
        }

        @Override
        public boolean isHandled(RecipeEntry<SmithingRecipe> recipeEntry) {
            return JeiPluginImpl.isTrimming(recipeEntry.value());
        }
        //?} else {
        /*@Override
        public @NotNull RecipeType<SmithingRecipe> getRecipeType() {
            return JeiPluginImpl.TRIMMING;
        }

        @Override
        public boolean isHandled(SmithingRecipe recipe) {
            return JeiPluginImpl.isTrimming(recipe);
        }
        *///?}

        @Override
        public @NotNull Text getTitle() {
            return Text.translatable("emi.category.allthetrims.trimming");
        }

        @Override
        public @NotNull IDrawable getIcon() {
            return icon;
        }
    }
}