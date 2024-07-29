package com.bawnorton.allthetrims.client.render;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import com.bawnorton.allthetrims.client.colour.ARGBColourHelper;
import com.bawnorton.allthetrims.client.palette.TrimPalette;
import com.bawnorton.allthetrims.client.palette.TrimPalettes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;

//? if >1.20.6 {
/*import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.AnimalArmorItem;
*///?}

public final class ItemTrimColourProvider implements ItemColorProvider {
    private final TrimPalettes palettes;
    private final LayerData layerData;

    public ItemTrimColourProvider(TrimPalettes palettes, LayerData layerData) {
        this.palettes = palettes;
        this.layerData = layerData;
    }

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        //? if >1.20.6 {
        /*ArmorTrim trim = stack.getComponents().get(DataComponentTypes.TRIM);
        *///?} else {
        ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
        if (networkHandler == null) return -1;

        DynamicRegistryManager registryManager = networkHandler.getRegistryManager();
        ArmorTrim trim = ArmorTrim.getTrim(registryManager, stack).orElse(null);
        //?}
        if(trim == null) return -1;

        ArmorTrimMaterial material = trim.getMaterial().value();
        if(!(material.assetName().equals(AllTheTrims.DYNAMIC) || AllTheTrimsClient.getConfig().overrideExisting)) return -1;

        int startLayer = layerData.getTrimStartLayer(stack.getItem());
        if(tintIndex < startLayer) return -1;

        Item materialItem = material.ingredient().value();
        TrimPalette palette = palettes.getOrGeneratePalette(materialItem);

        return ARGBColourHelper.fullAlpha(palette.getColours().get(tintIndex - startLayer));
    }

    public Item[] getApplicableItems() {
        return Registries.ITEM.stream().filter(item -> {
            if(item instanceof Equipment equipment) {
                //? if >1.20.6
                /*if(item instanceof AnimalArmorItem) return false;*/
                return equipment.getSlotType().isArmorSlot();
            }
            return false;
        }).toArray(Item[]::new);
    }
}