package com.bawnorton.allthetrims.mixin.client;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.client.util.ImageUtil;
import com.bawnorton.allthetrims.client.util.PaletteHelper;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Optional;

@Mixin(ArmorTrim.class)
public abstract class ArmorTrimMixin {
    @Shadow
    public static Optional<ArmorTrim> getTrim(DynamicRegistryManager registryManager, ItemStack stack) {
        throw new AssertionError();
    }

    @ModifyExpressionValue(method = "appendTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/MutableText;append(Lnet/minecraft/text/Text;)Lnet/minecraft/text/MutableText;"))
    private static MutableText updateColour(MutableText original, ItemStack stack, DynamicRegistryManager registryManager, List<Text> tooltip) {
        ArmorTrim trim = getTrim(registryManager, stack).orElseThrow(AssertionError::new);
        ArmorTrimMaterial material = trim.getMaterial().value();
        RegistryEntry<Item> ingredient = material.ingredient();
        String assetName = material.assetName();
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
