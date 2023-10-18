package com.bawnorton.allthetrims.mixin.fabric.client.bclib;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.annotation.ConditionalMixin;
import com.bawnorton.allthetrims.json.JsonHelper;
import com.bawnorton.allthetrims.util.DebugHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Equipment;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.betterx.bclib.client.models.PatternsHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Pseudo
@Mixin(value = PatternsHelper.class, remap = false)
@ConditionalMixin(modid = "bclib")
public abstract class PatternsHelperMixin {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @ModifyReturnValue(method = "createItemGenerated", at = @At("RETURN"))
    private static Optional<String> addTrimsToArmourItems(Optional<String> original, Identifier identifier) {
        if (original.isEmpty()) return original;

        Identifier noInv = identifier.withPath(identifier.getPath().replace("#inventory", ""));
        if (!(Registries.ITEM.get(noInv) instanceof Equipment equipment)) return original;

        String armourType = switch (equipment.getSlotType()) {
            case HEAD -> "helmet";
            case CHEST -> "chestplate";
            case LEGS -> "leggings";
            case FEET -> "boots";
            case MAINHAND, OFFHAND -> null;
        };
        if (armourType == null) {
            AllTheTrims.LOGGER.warn("Item " + noInv + "'s slot type is not an armour slot type, skipping");
            return original;
        }
        if (equipment instanceof ElytraItem && !FabricLoader.getInstance().isModLoaded("elytratrims")) {
            AllTheTrims.LOGGER.warn("Item " + noInv + " is an elytra, but elytratrims is not loaded, skipping");
            return original;
        }

        JsonObject model = JsonHelper.fromJsonString(original.get(), JsonObject.class);
        if (!model.has("textures")) {
            AllTheTrims.LOGGER.warn("Item " + noInv + "'s model does not have a textures parameter, skipping");
            return original;
        }

        JsonObject textures = model.get("textures").getAsJsonObject();
        if (!textures.has("layer0")) {
            AllTheTrims.LOGGER.warn("Item " + noInv + "'s model does not have a layer0 texture, skipping");
            return original;
        }

        String baseTexture = textures.get("layer0").getAsString();
        JsonArray overrides;
        if (model.has("overrides")) {
            overrides = model.get("overrides").getAsJsonArray();
        } else {
            overrides = new JsonArray();
            model.add("overrides", overrides);
        }
        JsonObject attOverride = new JsonObject();
        attOverride.addProperty("model", baseTexture + "_" + AllTheTrims.TRIM_ASSET_NAME + "_trim");
        JsonObject predicate = new JsonObject();
        predicate.addProperty("trim_type", "dynamic");
        attOverride.add("predicate", predicate);
        overrides.add(attOverride);

        DebugHelper.createDebugFile("bclib", noInv + ".json", JsonHelper.toJsonString(model));
        return Optional.of(JsonHelper.toJsonString(model));
    }
}
