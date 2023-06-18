package com.bawnorton.allthetrims.mixin;

import com.bawnorton.allthetrims.AllTheTrims;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.resource.Resource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Iterator;
import java.util.Map;

@Debug(export = true)
@Mixin(RegistryLoader.class)
public abstract class RegistryLoaderMixin {
    // pure cursedness
    @ModifyExpressionValue(method = "load(Lnet/minecraft/registry/RegistryOps$RegistryInfoGetter;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/registry/MutableRegistry;Lcom/mojang/serialization/Decoder;Ljava/util/Map;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceFinder;findResources(Lnet/minecraft/resource/ResourceManager;)Ljava/util/Map;"))
    private static Map<Identifier, Resource> addAllTrimMaterialJsonFiles(Map<Identifier, Resource> original) {
        Iterator<Map.Entry<Identifier, Resource>> iterator = original.entrySet().iterator();
        if (!iterator.hasNext()) return original;

        Map.Entry<Identifier, Resource> first = original.entrySet().iterator().next();
        if (!first.getKey().getPath().contains("trim_material")) return original;

        int max = (Registries.ITEM.getIds().size() + 10) * 10; // index can't be greater than 1 and 0.1 to 1 are reserved for vanilla, this reduces the chance of a colision with vanilla or another mod
        float index = 1f / max;
        for (Item item : Registries.ITEM) {
            if (AllTheTrims.isUsedAsMaterial(item)) continue;
            Identifier itemId = Registries.ITEM.getId(item);
            String resourceString =
                    """
                                    {
                                      "asset_name": "att_blank",
                                      "description": {
                                        "color": "%s",
                                        "translate": "%s %s"
                                      },
                                      "ingredient": "%s",
                                      "item_model_index": %f
                                    }
                            """.formatted(
                            "#FFFFFF",
                            item.getName().getString(), Text.translatable("text.allthetrims.material").getString(),
                            itemId,
                            index
                    );
            Resource resource = new Resource(first.getValue().getPack(), () -> IOUtils.toInputStream(resourceString, "UTF-8"));
            Identifier resourceId = new Identifier(itemId.getNamespace(), "trim_material/" + itemId.getPath() + ".json");
            original.put(resourceId, resource);
            index += 1f / max;
        }
        return original;
    }
}