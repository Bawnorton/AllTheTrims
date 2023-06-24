package com.bawnorton.allthetrims.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.render.model.json.ItemModelGenerator;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(value = ItemModelGenerator.class, remap = false)
public abstract class ItemModelGeneratorMixin {
    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayList([Ljava/lang/Object;)Ljava/util/ArrayList;"))
    private static ArrayList<String> increaseLayerCount(ArrayList<String> original) {
        for(int i = 5; i < 11; i++) {
            if(original.contains("layer" + i)) {
                continue;
            }
            original.add("layer" + i);
        }
        return original;
    }
}
