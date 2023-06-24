package com.bawnorton.allthetrims.mixin.client;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.util.ImageUtil;
import com.bawnorton.allthetrims.util.PaletteHelper;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.item.Item;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ArmorTrimMaterial.class)
public abstract class ArmorTrimMaterialMixin {
    @Shadow
    @Final
    private RegistryEntry<Item> ingredient;

    @Shadow @Final private String assetName;

    @ModifyReturnValue(method = "description", at = @At("RETURN"))
    private Text updateColour(Text original) {
        if(AllTheTrims.notWhitelisted(ingredient.value())) return Text.translatable("item.allthetrims.materials.unavailable").setStyle(original.getStyle().withColor(Formatting.RED));
        Item trimItem = ingredient.value();
        Identifier trimAssetId = new Identifier(Registries.ITEM.getId(trimItem).getNamespace(), assetName);
        if(PaletteHelper.paletteExists(trimAssetId)) {
            return original;
        } else {
            Style style = original.getStyle().withColor(ImageUtil.getAverageColour(PaletteHelper.getPalette(trimItem)).getRGB());
            return Text.literal(original.getString()).setStyle(style);
        }
    }
}
