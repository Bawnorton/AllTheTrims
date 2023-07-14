package com.bawnorton.allthetrims.mixin.client;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.json.JsonHelper;
import com.bawnorton.allthetrims.util.DebugHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mixin(NamespaceResourceManager.class)
public abstract class NamespaceResourceManagerMixin {
    @ModifyReturnValue(method = "getAllResources", at = @At("RETURN"))
    private List<Resource> replaceTrimMaterials(List<Resource> original, Identifier identifier) {
        if(!identifier.toString().contains("armour_trims")) return original;

        List<Resource> newResources = new ArrayList<>();
        for (Resource resource : original) {
            try (BufferedReader reader = resource.getReader()) {
                JsonObject atlas = JsonHelper.fromJsonReader(reader, JsonObject.class);
                if(!atlas.has("sources")) return original;

                JsonArray sources = atlas.getAsJsonArray("sources");
                if(sources.size() == 0) return original;

                for(JsonElement source: sources) {
                    if(!(source instanceof JsonObject sourceJson)) continue;
                    if(!sourceJson.has("permutations")) continue;
                    if(!sourceJson.has("type")) continue;

                    String type = sourceJson.get("type").getAsString();
                    if(!type.equals("paletted_permutations")) continue;

                    JsonObject permutations = new JsonObject();
                    permutations.addProperty(AllTheTrims.TRIM_ASSET_NAME, "trims/color_palettes/blank");
                    sourceJson.add("permutations", permutations);
                }
                newResources.add(new Resource(resource.getPack(), () -> IOUtils.toInputStream(JsonHelper.toJsonString(atlas), "UTF-8")));

                DebugHelper.createDebugFile("atlases", resource.getResourcePackName() + "_armour_trims.json", JsonHelper.toJsonString(atlas));
            } catch (RuntimeException | IOException e) {
                AllTheTrims.LOGGER.error("Failed to modify trim atlas: " + identifier);
                return original;
            }
        }
        return newResources;
    }
}
