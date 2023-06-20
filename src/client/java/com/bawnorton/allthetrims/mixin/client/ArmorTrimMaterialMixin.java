package com.bawnorton.allthetrims.mixin.client;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.util.ImageUtil;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.item.Item;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ArmorTrimMaterial.class)
public abstract class ArmorTrimMaterialMixin {
    @Shadow
    @Final
    private RegistryEntry<Item> ingredient;

    @ModifyReturnValue(method = "description", at = @At("RETURN"))
    private Text updateColour(Text original) {
        if(AllTheTrims.isUsedAsMaterial(ingredient.value())) return original;
        if(AllTheTrims.notWhitelisted(ingredient.value())) return Text.translatable("item.allthetrims.materials.unavailable").setStyle(original.getStyle().withColor(Formatting.RED));
        return Text.literal(original.getString()).setStyle(original.getStyle().withColor(ImageUtil.getAverageColour(ingredient.value()).getRGB()));
    }
}
