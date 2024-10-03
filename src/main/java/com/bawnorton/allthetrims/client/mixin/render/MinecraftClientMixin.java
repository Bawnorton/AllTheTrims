package com.bawnorton.allthetrims.client.mixin.render;

import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import com.bawnorton.allthetrims.client.mixin.accessor.ItemColorsAccessor;
import com.bawnorton.allthetrims.client.palette.TrimPalette;
import com.bawnorton.allthetrims.client.palette.TrimPalettes;
import com.bawnorton.allthetrims.client.render.ItemTrimColourProvider;
import com.bawnorton.allthetrims.client.render.LayerData;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.collection.IdList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.HashMap;
import java.util.Map;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @ModifyExpressionValue(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/color/item/ItemColors;create(Lnet/minecraft/client/color/block/BlockColors;)Lnet/minecraft/client/color/item/ItemColors;"
            )
    )
    private ItemColors registerTrimColourProvider(ItemColors original) {
        TrimPalettes trimPalettes = AllTheTrimsClient.getTrimPalettes();
        LayerData layerData = AllTheTrimsClient.getLayerData();
        //? if fabric {
        /*IdList<ItemColorProvider> providers = new IdList<>();
        IdList<ItemColorProvider> existingProviders = ((ItemColorsAccessor) original).getProviders();
        Registries.ITEM.stream()
                .forEach(item -> {
                    int rawId = Registries.ITEM.getRawId(item);
                    ItemColorProvider existingProvider = existingProviders.get(rawId);
                    providers.set(existingProvider, rawId);
                });
        ItemTrimColourProvider colourRenderer = new ItemTrimColourProvider(trimPalettes, layerData, providers);
        *///?} elif neoforge {
        Map<Item, ItemColorProvider> providers = new HashMap<>();
        Map<Item, ItemColorProvider> existingProviders = ((ItemColorsAccessor) original).getProviders();
        Registries.ITEM.stream()
                .forEach(item -> {
                    ItemColorProvider existingProvider = existingProviders.get(item);
                    providers.put(item, existingProvider);
                });
        ItemTrimColourProvider colourRenderer = new ItemTrimColourProvider(trimPalettes, layerData, providers);
        //?}
        original.register(colourRenderer, colourRenderer.getApplicableItems());
        return original;
    }

    @Inject(
            method = "render",
            at = @At("TAIL")
    )
    private void cycleAnimatedTrims(CallbackInfo ci) {
        if(AllTheTrimsClient.getConfig().animate) {
            AllTheTrimsClient.getTrimPalettes().forEach(TrimPalette::cycleAnimatedColours);
        }
    }
}
