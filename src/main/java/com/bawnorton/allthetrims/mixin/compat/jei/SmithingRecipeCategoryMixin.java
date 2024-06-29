package com.bawnorton.allthetrims.mixin.compat.jei;

import com.bawnorton.allthetrims.client.compat.recipebrowser.jei.JeiPluginImpl;
import com.bawnorton.allthetrims.util.mixin.ConditionalMixin;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mezz.jei.library.plugins.vanilla.anvil.SmithingRecipeCategory;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.SmithingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings("UnusedMixin")
@Mixin(SmithingRecipeCategory.class)
@ConditionalMixin("jei")
public abstract class SmithingRecipeCategoryMixin {
    @ModifyReturnValue(method = "isHandled(Lnet/minecraft/recipe/RecipeEntry;)Z", at = @At("RETURN"))
    private boolean onlyHandleTransformations(boolean original, RecipeEntry<SmithingRecipe> recipeEntry) {
        return !JeiPluginImpl.isTrimming(recipeEntry);
    }
}