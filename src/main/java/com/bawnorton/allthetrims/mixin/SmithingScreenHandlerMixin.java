package com.bawnorton.allthetrims.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.SmithingScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SmithingScreenHandler.class)
public abstract class SmithingScreenHandlerMixin {
    @ModifyReturnValue(method = "getSlotFor", at = @At("RETURN"))
    private int correctTemplateSlot(int original, ItemStack stack) {
        if(stack.isIn(ItemTags.TRIM_TEMPLATES) || stack.getItem().getTranslationKey().contains("smithing_template")) return 0;
        return original;
    }
}
