package com.bawnorton.allthetrims.mixin.client;

import com.bawnorton.allthetrims.util.ArmorTrimAtlas;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.texture.atlas.AtlasLoader;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(AtlasLoader.class)
public abstract class AtlasLoaderMixin {
    @Inject(method = "of", at = @At("HEAD"))
    private static void captureIdentifier(ResourceManager resourceManager, Identifier id, CallbackInfoReturnable<AtlasLoader> cir, @Share("id") LocalRef<Identifier> idRef) {
        idRef.set(id);
    }

    @ModifyExpressionValue(method = "of", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceManager;getAllResources(Lnet/minecraft/util/Identifier;)Ljava/util/List;"))
    private static List<Resource> addAllTrimMaterials(List<Resource> resourceList, @Share("id") LocalRef<Identifier> idRef) {
        if(idRef.get().getPath().contains("armor_trims")) {
            List<Resource> newResources = new ArrayList<>();
            for(Resource resource: resourceList) {
                try(BufferedReader reader = resource.getReader()) {
                    ArmorTrimAtlas atlas = ArmorTrimAtlas.fromJson(reader);
                    ArmorTrimAtlas.Source source = atlas.sources.get(0);
                    Map<String, String> permuations = source.permutations;
                    for(Item item: Registries.ITEM) {
                        Identifier itemId = Registries.ITEM.getId(item);
                        String key = "allthetrims_" + item.getTranslationKey().replace(".", "_");
                        String value = "trims/color_palettes/" + itemId.getNamespace() + "/" + itemId.getPath();
                        permuations.put(key, value);
                    }
                    newResources.add(new Resource(resource.getPack(), () -> ArmorTrimAtlas.toJson(atlas)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            resourceList = newResources;
        }
        return resourceList;
    }
}
