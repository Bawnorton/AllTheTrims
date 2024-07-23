package com.bawnorton.allthetrims.mixin.compat.fabric.bclib;

//? if fabric {

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import com.bawnorton.allthetrims.client.mixin.accessor.JsonUnbakedModelAccessor;
import com.bawnorton.allthetrims.client.model.item.JsonParser;
import com.bawnorton.allthetrims.client.model.item.adapter.DefaultTrimModelLoaderAdapter;
import com.bawnorton.allthetrims.client.model.item.json.TextureLayers;
import com.bawnorton.allthetrims.client.model.item.json.TrimmableItemModel;
import com.bawnorton.allthetrims.util.mixin.ConditionalMixin;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.betterx.bclib.client.models.CustomModelBakery;
import org.betterx.bclib.interfaces.ItemModelProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

// If only there was a mod that needed a dedicated compat from every other mod
// BCLib:
@SuppressWarnings("UnusedMixin")
@Mixin(CustomModelBakery.class)
@ConditionalMixin("bclib")
public abstract class CustomModelBakeryMixin {
    @Unique
    private final DefaultTrimModelLoaderAdapter trimModelLoaderAdapter = new DefaultTrimModelLoaderAdapter();
    @Unique
    private final JsonParser parser = new JsonParser();

    @WrapOperation(
            method = "addItemModel",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/betterx/bclib/interfaces/ItemModelProvider;getItemModel(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/model/json/JsonUnbakedModel;"
            )
    )
    private JsonUnbakedModel addTrimsToBCLibModel(ItemModelProvider instance, Identifier id, Operation<JsonUnbakedModel> original) {
        JsonUnbakedModel model = original.call(instance, id);
        Item item = Registries.ITEM.get(id);
        if(!trimModelLoaderAdapter.canTrim(item)) return model;

        model.getOverrides().add(
                new ModelOverride(id, List.of(
                        new ModelOverride.Condition(
                                Identifier.ofVanilla("trim_type"),
                                AllTheTrims.MODEL_INDEX
                        )
                ))
        );

        JsonUnbakedModelAccessor accessor = (JsonUnbakedModelAccessor) model;
        Map<String, Either<SpriteIdentifier, String>> textures = new HashMap<>(accessor.getTextureMap());
        AllTheTrimsClient.getLayerData().setTrimStartLayer(item, textures.size());

        String[] layers = new String[textures.size()];
        for (Map.Entry<String, Either<SpriteIdentifier, String>> entry : textures.entrySet()) {
            String layer = entry.getKey();
            Either<SpriteIdentifier, String> location = entry.getValue();

            String layerLocation = location.map(spriteId -> spriteId.getTextureId().toString(), Function.identity());
            int layerNum = Integer.parseInt(layer.replaceAll("\\D", ""));
            layers[layerNum] = layerLocation;
        }

        int layerCount = trimModelLoaderAdapter.getLayerCount(item);
        List<String> existingAndTrims = new ArrayList<>(layers.length + layerCount);
        existingAndTrims.addAll(Arrays.asList(layers));
        for(int i = 0; i < layerCount; i++) {
            existingAndTrims.add(trimModelLoaderAdapter.getLayerName(item, i));
        }

        TrimmableItemModel itemModel = TrimmableItemModel.builder()
                .parent(accessor.getParentId())
                .textures(TextureLayers.of(existingAndTrims))
                .build();

        return JsonUnbakedModel.deserialize(parser.toJson(itemModel));
    }
}

//?}