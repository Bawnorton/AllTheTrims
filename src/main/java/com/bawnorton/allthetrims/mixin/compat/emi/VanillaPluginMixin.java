package com.bawnorton.allthetrims.mixin.compat.emi;

import com.bawnorton.allthetrims.util.mixin.ConditionalMixin;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.emi.emi.VanillaPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;
import java.util.function.Supplier;

@SuppressWarnings("UnusedMixin")
@Mixin(VanillaPlugin.class)
@ConditionalMixin("emi")
public abstract class VanillaPluginMixin {
    @WrapOperation(
            method = "register",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/emi/emi/VanillaPlugin;addRecipeSafe(Ldev/emi/emi/api/EmiRegistry;Ljava/util/function/Supplier;Lnet/minecraft/recipe/Recipe;)V",
                    ordinal = 0
            ),
            slice = @Slice(
                    from = @At(
                            value = "CONSTANT",
                            args = "classValue=dev.emi.emi.mixin.accessor.SmithingTrimRecipeAccessor"
                    )
            )
    )
    private void dontAddSmithingTrimRecipe(EmiRegistry instance, Supplier<EmiRecipe> registry, Recipe<?> supplier, Operation<Void> original) {
    }
}
