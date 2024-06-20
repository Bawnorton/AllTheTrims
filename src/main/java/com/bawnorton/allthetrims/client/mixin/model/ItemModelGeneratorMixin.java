package com.bawnorton.allthetrims.client.mixin.model;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.render.model.json.ItemModelGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import java.util.ArrayList;

@Mixin(value = ItemModelGenerator.class)
public abstract class ItemModelGeneratorMixin {
    @ModifyExpressionValue(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/Lists;newArrayList([Ljava/lang/Object;)Ljava/util/ArrayList;",
                    remap = false
            )
    )
    private static ArrayList<String> increaseLayerCount(ArrayList<String> original) {
        for (int i = 5; i < 20; i++) { // should cover all possible armour / trim layers
            if (original.contains("layer" + i)) {
                continue;
            }
            original.add("layer" + i);
        }
        return original;
    }
}