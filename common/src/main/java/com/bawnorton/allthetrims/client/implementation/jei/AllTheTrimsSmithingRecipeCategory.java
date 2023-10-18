package com.bawnorton.allthetrims.client.implementation.jei;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.library.plugins.vanilla.anvil.SmithingRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class AllTheTrimsSmithingRecipeCategory extends SmithingRecipeCategory {
    private final IDrawable icon;

    public AllTheTrimsSmithingRecipeCategory(IGuiHelper guiHelper) {
        super(guiHelper);
        icon = guiHelper.createDrawableItemStack(new ItemStack(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE));
    }

    @Override
    public @NotNull RecipeType<SmithingRecipe> getRecipeType() {
        return JeiPluginImpl.smithingRecipeType;
    }

    @Override
    public @NotNull Text getTitle() {
        return Text.translatable("emi.category.allthetrims.smithing");
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    @Override
    public boolean isHandled(SmithingRecipe recipe) {
        return JeiPluginImpl.isTrimmingRecipe(recipe);
    }
}