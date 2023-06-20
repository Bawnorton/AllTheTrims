package com.bawnorton.allthetrims.mixin;

import com.bawnorton.allthetrims.compat.Compat;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(ElytraItem.class)
public abstract class ElytraItemMixin extends ItemMixin {
    @Override
    protected void appendMissingElytraTrimsTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        if(Compat.isElytraTrimsLoaded() || world == null) return;

        Optional<ArmorTrim> trim = ArmorTrim.getTrim(world.getRegistryManager(), stack);
        if(trim.isEmpty()) return;

        tooltip.add(Text.translatable("tooltip.elytratrims.missing"));
    }
}
