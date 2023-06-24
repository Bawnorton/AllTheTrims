package com.bawnorton.allthetrims.mixin.client;

import com.bawnorton.allthetrims.util.ResourceHelper;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(NamespaceResourceManager.class)
public abstract class NamespaceResourceManagerMixin {
    @ModifyReturnValue(method = "getAllResources", at = @At("RETURN"))
    private List<Resource> replaceTrimMaterials(List<Resource> original, Identifier identifier) {
        return ResourceHelper.replaceTrimMaterials(original, identifier);
    }
}
