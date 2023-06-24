package com.bawnorton.allthetrims.mixin.client;

import net.minecraft.client.render.model.json.ItemModelGenerator;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;

@Mixin(ItemModelGenerator.class)
public abstract class ItemModelGeneratorMixin {
    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayList([Ljava/lang/Object;)Ljava/util/ArrayList;"))
    private static <E> ArrayList<String> increaseLayerCount(E[] elements) {
        return Util.make(new ArrayList<>(), list -> {
            for (int i = 0; i < 11; i++) list.add("layer" + i);
        });
    }
}
