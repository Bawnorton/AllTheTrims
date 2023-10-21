package com.bawnorton.allthetrims.mixin;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.json.JsonHelper;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Mixin(RegistryLoader.class)
public abstract class RegistryLoaderMixin {
    // pure cursedness
    @ModifyExpressionValue(method = "load(Lnet/minecraft/registry/RegistryOps$RegistryInfoGetter;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/registry/MutableRegistry;Lcom/mojang/serialization/Decoder;Ljava/util/Map;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceFinder;findResources(Lnet/minecraft/resource/ResourceManager;)Ljava/util/Map;"))
    private static Map<Identifier, Resource> addAllTrimMaterialJsonFiles(Map<Identifier, Resource> original) {
        Iterator<Map.Entry<Identifier, Resource>> iterator = original.entrySet().iterator();
        if (!iterator.hasNext()) return original;

        Map.Entry<Identifier, Resource> first = original.entrySet().iterator().next();
        if (!first.getKey().getPath().contains("trim_material")) return original;

        Set<Identifier> seenIngredients = new HashSet<>();
        for (Map.Entry<Identifier, Resource> resourceEntry : original.entrySet()) {
            try(BufferedReader reader = resourceEntry.getValue().getReader()) {
                JsonObject trimJson = JsonHelper.fromJsonReader(reader, JsonObject.class);
                seenIngredients.add(new Identifier(trimJson.get("ingredient").getAsString()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        TrimMaterialHelper.loopTrimMaterials((item) -> {
            Identifier itemId = Registries.ITEM.getId(item);
            if (seenIngredients.contains(itemId)) return;

            JsonObject resourceJson = new JsonObject();
            try {
                resourceJson.addProperty("asset_name", AllTheTrims.TRIM_ASSET_NAME);
                JsonObject description = new JsonObject();
                description.addProperty("color", "#FFFFFF");
                description.addProperty("translate", allTheTrims$escape(item.getName()
                                                                            .getString()) + " " + allTheTrims$escape(Text.translatable("text.allthetrims.material")
                                                                                                                         .getString()));
                resourceJson.add("description", description);
                resourceJson.addProperty("ingredient", itemId.toString());
                resourceJson.addProperty("item_model_index", Float.MAX_VALUE);
            } catch (RuntimeException e) {
                AllTheTrims.LOGGER.error("Failed to generate trim material JSON for " + itemId, e);
                return;
            }

            Resource resource = new Resource(first.getValue()
                                                 .getPack(), () -> IOUtils.toInputStream(resourceJson.toString(), "UTF-8"));
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