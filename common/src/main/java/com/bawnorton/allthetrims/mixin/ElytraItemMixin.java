package com.bawnorton.allthetrims.mixin;

import com.bawnorton.allthetrims.annotation.ConditionalMixin;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(ElytraItem.class)
@ConditionalMixin(modid = "elytratrims", applyIfPresent = false)
public abstract class ElytraItemMixin extends ItemMixin {
    @Override
    protected void appendMissingElytraTrimsTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        if (world == null) return;

        Optional<ArmorTrim> trim = ArmorTrim.getTrim(world.getRegistryManager(), stack);
        if (trim.isEmpty()) return;

        tooltip.add(Text.translatable("tooltip.elytratrims.missing"));
    }
}
