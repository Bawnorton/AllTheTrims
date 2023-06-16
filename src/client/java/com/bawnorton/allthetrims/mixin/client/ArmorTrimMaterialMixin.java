package com.bawnorton.allthetrims.mixin.client;

import com.bawnorton.allthetrims.util.ImageUtil;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.item.Item;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
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
        return Text.literal(original.getString()).setStyle(original.getStyle().withColor(ImageUtil.getMedianColour(ingredient.value()).getRGB()));
    }
}
