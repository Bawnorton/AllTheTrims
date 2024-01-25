package com.bawnorton.allthetrims.mixin.client;

import com.bawnorton.allthetrims.Compat;
import com.bawnorton.allthetrims.client.util.ImageUtil;
import com.bawnorton.allthetrims.client.util.PaletteHelper;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Optional;

@Debug(export = true)
@Mixin(ArmorTrim.class)
public abstract class ArmorTrimMixin {
    @Shadow
    public static Optional<ArmorTrim> getTrim(DynamicRegistryManager registryManager, ItemStack stack) {
        throw new AssertionError();
    }

    @WrapOperation(method = "appendTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/MutableText;append(Lnet/minecraft/text/Text;)Lnet/minecraft/text/MutableText;"))
    private static MutableText updateColour(MutableText instance, Text text, Operation<MutableText> original, ItemStack stack, DynamicRegistryManager registryManager, List<Text> tooltip) {
        ArmorTrim trim = getTrim(registryManager, stack).orElseThrow(AssertionError::new);
        ArmorTrimMaterial material = trim.getMaterial().value();
        RegistryEntry<Item> ingredient = material.ingredient();
        String assetName = material.assetName();
        Item trimItem = ingredient.value();
        Identifier trimAssetId = new Identifier(Registries.ITEM.getId(trimItem).getNamespace(), assetName);
        MutableText originalText = original.call(instance, text);
        return originalText.styled(style -> PaletteHelper.paletteExists(trimAssetId) ? style : style.withColor(ImageUtil.getAverageColour(PaletteHelper.getPalette(trimItem))
                                                                                                                        .getRGB()));
    }

    @WrapWithCondition(method = "appendTooltip", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 2))
    private static <E> boolean dontAddMaterialIfBetterTrimTooltipsLoaded(List<E> instance, E e) {
        return !Compat.isBetterTrimTooltipsIsLoaded();
    }
}
