package com.bawnorton.allthetrims.mixin.client;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.json.AtlasJson;
import com.bawnorton.allthetrims.json.JsonRepresentable;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.texture.atlas.AtlasLoader;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(AtlasLoader.class)
public abstract class AtlasLoaderMixin {
    @ModifyExpressionValue(method = "of", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceManager;getAllResources(Lnet/minecraft/util/Identifier;)Ljava/util/List;"))
    private static List<Resource> addAllTrimMaterials(List<Resource> resourceList, ResourceManager resourceManager, Identifier id) {
        if (id.getPath().contains("armor_trims")) {
            List<Resource> newResources = new ArrayList<>();
            for (Resource resource : resourceList) {
                try (BufferedReader reader = resource.getReader()) {
                    AtlasJson atlas = JsonRepresentable.fromJson(reader, AtlasJson.class);
                    AtlasJson.Source permutationSource = atlas.sources.get(0);
                    Map<String, String> permuations = permutationSource.permutations;
                    permuations.put("att_blank", "trims/color_palettes/blank");
                    newResources.add(new Resource(resource.getPack(), atlas::toInputStream));
                } catch (RuntimeException | IOException e) {
                    AllTheTrims.LOGGER.error("Failed to modify trim atlas: " + id);
                    return resourceList;
                }
            }
            resourceList = newResources;
        }
        return resourceList;
    }
}
