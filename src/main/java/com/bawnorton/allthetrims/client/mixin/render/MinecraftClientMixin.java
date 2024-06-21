package com.bawnorton.allthetrims.client.mixin.render;

import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import com.bawnorton.allthetrims.client.palette.TrimPalette;
import com.bawnorton.allthetrims.client.palette.TrimPalettes;
import com.bawnorton.allthetrims.client.render.ItemTrimColourProvider;
import com.bawnorton.allthetrims.client.render.LayerData;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.item.ItemColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
        ItemTrimColourProvider colourRenderer = new ItemTrimColourProvider(trimPalettes, layerData);
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
