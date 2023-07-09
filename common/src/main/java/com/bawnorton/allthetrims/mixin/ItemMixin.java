package com.bawnorton.allthetrims.mixin;

import com.bawnorton.allthetrims.annotation.ConditionalMixin;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Item.class)
@ConditionalMixin(modid = "elytratrims", applyIfPresent = false)
public abstract class ItemMixin {
    @Inject(method = "appendTooltip", at = @At("HEAD"))
    protected void appendMissingElytraTrimsTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        // overriden in EltyraItemMixin
    }
}
