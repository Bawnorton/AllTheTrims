package com.bawnorton.allthetrims.mixin;

import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.item.trim.ArmorTrimMaterials;
import net.minecraft.registry.Registerable;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(ArmorTrimMaterials.class)
public abstract class ArmorTrimMaterialsMixin {
    @Inject(method = "bootstrap", at = @At("TAIL"))
    private static void addAllOtherItems(Registerable<ArmorTrimMaterial> registry, CallbackInfo ci) {
        System.out.println("Test 2");
    }
}
