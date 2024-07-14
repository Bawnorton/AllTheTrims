package com.bawnorton.allthetrims.client.mixin.model;

import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import java.util.Map;

@Mixin(BakedModelManager.class)
public abstract class BakedModelManagerMixin {
    @ModifyExpressionValue(
            method = "method_45895",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resource/ResourceFinder;findResources(Lnet/minecraft/resource/ResourceManager;)Ljava/util/Map;"
            )
    )
    private static Map<Identifier, Resource> addDynamicTrimModel(Map<Identifier, Resource> original) {
        return AllTheTrimsClient.getItemModelLoader().loadModels(original);
    }
}
