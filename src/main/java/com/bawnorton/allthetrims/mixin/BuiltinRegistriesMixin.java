package com.bawnorton.allthetrims.mixin;

import net.minecraft.registry.BuiltinRegistries;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;

@Debug(export = true)
@Mixin(BuiltinRegistries.class)
public abstract class BuiltinRegistriesMixin {
    static {
        System.out.println("Test");
    }
}
