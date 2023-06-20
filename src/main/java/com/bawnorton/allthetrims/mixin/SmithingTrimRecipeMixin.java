package com.bawnorton.allthetrims.mixin;

import com.bawnorton.allthetrims.AllTheTrims;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.SmithingTrimRecipe;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SmithingTrimRecipe.class)
public abstract class SmithingTrimRecipeMixin {
    @ModifyExpressionValue(method = "testAddition", at = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/Ingredient;test(Lnet/minecraft/item/ItemStack;)Z"))
    private boolean checkWhitelist(boolean original, ItemStack stack) {
        Item material = stack.getItem();
        return original && (!AllTheTrims.notWhitelisted(material) || AllTheTrims.isUsedAsMaterial(material));
    }

    @ModifyExpressionValue(method = "matches", at = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/Ingredient;test(Lnet/minecraft/item/ItemStack;)Z", ordinal = 2))
    private boolean checkWhitelist(boolean original, Inventory inventory, World world) {
        Item material = inventory.getStack(2).getItem();
        return original && (!AllTheTrims.notWhitelisted(material) || AllTheTrims.isUsedAsMaterial(material));
    }
}
