package com.bawnorton.allthetrims.client.mixin.accessor;

import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.trim.ArmorTrim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ArmorTrim.class)
public interface ArmorTrimAccessor {
    //? if <1.20.6 {
    @Invoker
    String callGetMaterialAssetNameFor(ArmorMaterial armourMaterial);
    //?}
}
