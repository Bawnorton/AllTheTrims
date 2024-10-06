package com.bawnorton.allthetrims.client.render;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import com.bawnorton.allthetrims.client.colour.ARGBColourHelper;
import com.bawnorton.allthetrims.client.palette.TrimPalette;
import com.bawnorton.allthetrims.client.palette.TrimPalettes;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.util.collection.IdList;

//? if >1.20.6 {
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.AnimalArmorItem;
import java.util.Map;
        //?} else {
/*import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.DynamicRegistryManager;
*///?}


public final class ItemTrimColourProvider implements ItemColorProvider {
    private final TrimPalettes palettes;
    private final LayerData layerData;
    //? if fabric {
    private final IdList<ItemColorProvider> existingProviders;

    public ItemTrimColourProvider(TrimPalettes palettes, LayerData layerData, IdList<ItemColorProvider> existingProviders) {
    //?} elif neoforge {
    /*private final Map<Item, ItemColorProvider> existingProviders;

    public ItemTrimColourProvider(TrimPalettes palettes, LayerData layerData, Map<Item, ItemColorProvider> existingProviders) {
    *///?}
        this.palettes = palettes;
        this.layerData = layerData;
        this.existingProviders = existingProviders;
    }

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        //? if >1.20.6 {
        ArmorTrim trim = stack.getComponents().get(DataComponentTypes.TRIM);
        //?} else {
        /*ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
        if (networkHandler == null) return getExistingColor(stack, tintIndex);

        DynamicRegistryManager registryManager = networkHandler.getRegistryManager();
        ArmorTrim trim = ArmorTrim.getTrim(registryManager, stack).orElse(null);
        *///?}
        if(trim == null) return getExistingColor(stack, tintIndex);

        ArmorTrimMaterial material = trim.getMaterial().value();
        if(!(material.assetName().equals(AllTheTrims.DYNAMIC) || AllTheTrimsClient.getConfig().overrideExisting)) return -1;

        int startLayer = layerData.getTrimStartLayer(stack.getItem());
        if(tintIndex < startLayer) return getExistingColor(stack, tintIndex);

        Item materialItem = material.ingredient().value();
        TrimPalette palette = palettes.getOrGeneratePalette(materialItem);

        return ARGBColourHelper.fullAlpha(palette.getColours().get(tintIndex - startLayer));
    }

    private int getExistingColor(ItemStack stack, int tintIndex) {
        //? if fabric {
        ItemColorProvider existingProvider = existingProviders.get(Item.getRawId(stack.getItem()));
        //?} elif neoforge {
        /*ItemColorProvider existingProvider = existingProviders.get(stack.getItem());
        *///?}
        if (existingProvider == null)
            return -1;

        return existingProvider.getColor(stack, tintIndex);
    }

    public Item[] getApplicableItems() {
        return Registries.ITEM.stream().filter(item -> {
            if(item instanceof Equipment equipment) {
                //? if >1.20.6
                if(item instanceof AnimalArmorItem) return false;
                return equipment.getSlotType().isArmorSlot();
            }
            return false;
        }).toArray(Item[]::new);
    }
}