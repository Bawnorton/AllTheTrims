package com.bawnorton.allthetrims.mixin;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.json.JsonHelper;
import com.bawnorton.allthetrims.json.TrimMaterialJson;
import com.bawnorton.allthetrims.util.DebugHelper;
import com.bawnorton.allthetrims.util.TrimMaterialHelper;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.resource.Resource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

@Mixin(RegistryLoader.class)
public abstract class RegistryLoaderMixin {
    // pure cursedness
    @ModifyExpressionValue(method = "load(Lnet/minecraft/registry/RegistryOps$RegistryInfoGetter;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/registry/MutableRegistry;Lcom/mojang/serialization/Decoder;Ljava/util/Map;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceFinder;findResources(Lnet/minecraft/resource/ResourceManager;)Ljava/util/Map;"))
    private static Map<Identifier, Resource> addAllTrimMaterialJsonFiles(Map<Identifier, Resource> original) {
        Iterator<Map.Entry<Identifier, Resource>> iterator = original.entrySet().iterator();
        if (!iterator.hasNext()) return original;

        Map.Entry<Identifier, Resource> first = original.entrySet().iterator().next();
        if (!first.getKey().getPath().contains("trim_material")) return original;

        for (Map.Entry<Identifier, Resource> resourceEntry : original.entrySet()) {
            try(BufferedReader reader = resourceEntry.getValue().getReader()) {
                JsonObject trimJson = JsonHelper.fromJsonReader(reader, JsonObject.class);
                TrimMaterialHelper.BUILTIN_TRIM_MATERIALS.add(TrimMaterialJson.fromJson(trimJson));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        TrimMaterialHelper.forEachTrimMaterial((item, builtin) -> {
            if(builtin) return;

            Identifier itemId = Registries.ITEM.getId(item);
            TrimMaterialJson trimMaterialJson = new TrimMaterialJson(
                AllTheTrims.TRIM_ASSET_NAME,
                "#FFFFFF",
                allTheTrims$escape(item.getName().getString()) + " " + allTheTrims$escape(Text.translatable("text.allthetrims.material").getString()),
                itemId.toString(),
                Float.MAX_VALUE
            );
            JsonObject resourceJson = trimMaterialJson.asJson();
            Resource resource = new Resource(first.getValue().getPack(), () -> IOUtils.toInputStream(resourceJson.toString(), "UTF-8"));
            Identifier resourceId = new Identifier(itemId.getNamespace(), "trim_material/" + itemId.getPath() + ".json");
            original.put(resourceId, resource);

            DebugHelper.createDebugFile("trim_materials", itemId + ".json", resourceJson.toString());
        });
        return original;
    }

    @Unique
    private static String allTheTrims$escape(String string) {
        return string.replace("\"", "\\\"");
    }
}