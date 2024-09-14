package com.bawnorton.allthetrims.client.mixin.accessor;

import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.util.collection.IdList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemColors.class)
public interface ItemColorsAccessor {
    @Accessor
    IdList<ItemColorProvider> getProviders();
}
