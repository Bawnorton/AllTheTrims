package com.bawnorton.allthetrims.mixin;

import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.SmithingTrimRecipe;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Debug(export = true)
@Mixin(SmithingTrimRecipe.class)
public abstract class SmithingTrimRecipeMixin {
    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "<init>", at = @At("LOAD"), index = 4, argsOnly = true)
    private Ingredient setAdditionToAllItems(Ingredient addition) {
        return Ingredient.ofItems(Registries.ITEM.stream().toArray(Item[]::new));
    }
}
