package com.bawnorton.allthetrims.mixin.client;

import com.bawnorton.allthetrims.json.ArmorTrimAtlas;
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
        if(id.getPath().contains("armor_trims")) {
            List<Resource> newResources = new ArrayList<>();
            for(Resource resource: resourceList) {
                try(BufferedReader reader = resource.getReader()) {
                    ArmorTrimAtlas atlas = JsonRepresentable.fromJson(reader, ArmorTrimAtlas.class);
                    ArmorTrimAtlas.Source source = atlas.sources.get(0);
                    Map<String, String> permuations = source.permutations;
                    permuations.put("allthetrims_blank_trim", "trims/color_palettes/blank");
                    newResources.add(new Resource(resource.getPack(), atlas::toInputStream));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            resourceList = newResources;
        }
        return resourceList;
    }
}
