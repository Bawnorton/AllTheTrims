package com.bawnorton.allthetrims.client.mixin.accessor;

import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.item.Item;
import net.minecraft.util.collection.IdList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.Map;

@Mixin(ItemColors.class)
public interface ItemColorsAccessor {
    @Accessor
    //? if fabric {
    /*IdList<ItemColorProvider> getProviders();
    *///?} elif neoforge {
    Map<Item, ItemColorProvider> getProviders();
    //?}
}
