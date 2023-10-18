package com.bawnorton.allthetrims.mixin.client.emi;

import dev.emi.emi.VanillaPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import net.minecraft.recipe.Recipe;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.function.Supplier;

@Pseudo
@Mixin(VanillaPlugin.class)
public abstract class VanillaPluginMixin {
    @Redirect(method = "register", at = @At(value = "INVOKE", target = "Ldev/emi/emi/VanillaPlugin;addRecipeSafe(Ldev/emi/emi/api/EmiRegistry;Ljava/util/function/Supplier;Lnet/minecraft/recipe/Recipe;)V", ordinal = 1), slice = @Slice(from = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "net/minecraft/recipe/RecipeType.SMITHING : Lnet/minecraft/recipe/RecipeType;")))
    private void dontAddSmithingTrimRecipe(EmiRegistry e, Supplier<EmiRecipe> registry, Recipe<?> supplier) {
    }
}
