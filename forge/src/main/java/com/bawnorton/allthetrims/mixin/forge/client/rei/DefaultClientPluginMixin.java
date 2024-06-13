package com.bawnorton.allthetrims.mixin.forge.client.rei;

import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.plugin.client.DefaultClientPlugin;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.function.Function;

@Pseudo
@SuppressWarnings("UnstableApiUsage")
@Mixin(DefaultClientPlugin.class)
public abstract class DefaultClientPluginMixin {
    @Redirect(
            method = "registerDisplays",
            at = @At(
                    value = "INVOKE",
                    target = "Lme/shedaniel/rei/api/client/registry/display/DisplayRegistry;registerRecipesFiller(Ljava/lang/Class;Lnet/minecraft/recipe/RecipeType;Ljava/util/function/Function;)V",
                    ordinal = 0
            ),
            slice = @Slice(
                    from = @At(
                            value = "CONSTANT",
                            args = "classValue=net/minecraft/world/item/crafting/SmithingTrimRecipe"
                    )
            ),
            require = 0
    )
    private <T extends Recipe<?>, D extends Display> void dontRegisterSmithingTrimRecipeDisplay(DisplayRegistry instance, Class<T> typeClass, RecipeType<? super T> recipeType, Function<? extends T, @Nullable D> filler) {
    }

    @Redirect(
            method = "registerDisplays",
            at = @At(
                    value = "INVOKE",
                    target = "Lme/shedaniel/rei/api/client/registry/display/DisplayRegistry;registerRecipeFiller(Ljava/lang/Class;Lnet/minecraft/recipe/RecipeType;Ljava/util/function/Function;)V",
                    ordinal = 0
            ),
            slice = @Slice(
                    from = @At(
                            value = "CONSTANT",
                            args = "classValue=net/minecraft/world/item/crafting/SmithingTrimRecipe"
                    )
            ),
            require = 0
    )
    private <T extends Recipe<?>, D extends Display> void dontRegisterSmithingTrimRecipeDisplayOld(DisplayRegistry instance, Class<T> typeClass, RecipeType<? super T> recipeType, Function<? extends T, @Nullable D> filler) {
    }
}
