package com.bawnorton.allthetrims.fabric.mixin.client;

import com.bawnorton.allthetrims.annotation.ConditionalMixin;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.item.trim.ArmorTrim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.w3c.dom.Text;

import java.util.List;

@Mixin(ArmorTrim.class)
@ConditionalMixin(modid = "better-trim-tooltips")
public abstract class ArmorTrimMixin {
    @WrapWithCondition(method = "appendTooltip", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 2))
    private static boolean ignoreIfBetterTrimTooltipsPresent(List<Text> tooltip, Object text) {
        return true;
    }
}
