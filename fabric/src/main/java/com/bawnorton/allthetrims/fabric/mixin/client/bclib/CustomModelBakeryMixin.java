package com.bawnorton.allthetrims.fabric.mixin.client.bclib;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.annotation.ConditionalMixin;
import com.bawnorton.allthetrims.util.DebugHelper;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.betterx.bclib.client.models.CustomModelBakery;
import org.betterx.bclib.interfaces.ItemModelProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Pseudo
@Mixin(value = CustomModelBakery.class, remap = false)
@ConditionalMixin(modid = "bclib")
public abstract class CustomModelBakeryMixin {
    @Shadow
    @Final
    private Map<Identifier, UnbakedModel> models;

    @Inject(method = "addItemModel", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private void modifyItemModels(Identifier itemID, ItemModelProvider provider, CallbackInfo ci, @Local(name = "model") JsonUnbakedModel model) {
        AtomicBoolean isTrimmable = new AtomicBoolean(false);
        AtomicReference<Identifier> overrideId = new AtomicReference<>();
        model.getOverrides().forEach(modelOverride -> modelOverride.streamConditions().forEach(condition -> {
            if (!(condition.getType()
                           .equals(new Identifier("trim_type")) && (condition.getThreshold() >= Float.MAX_VALUE)))
                return;

            isTrimmable.set(true);
            overrideId.set(modelOverride.getModelId());
        }));
        if (!isTrimmable.get()) return;

        if (overrideId.get() == null) {
            AllTheTrims.LOGGER.error("Item " + itemID + " is marked as trimmable but has no model override id");
            return;
        }

        Item item = Registries.ITEM.get(itemID);
        if (!(item instanceof Equipment equipment)) {
            AllTheTrims.LOGGER.error("Item " + itemID + " is marked as trimmable but is not an equipment item");
            return;
        }

        String armourType = switch (equipment.getSlotType()) {
            case HEAD -> "helmet";
            case CHEST -> "chestplate";
            case LEGS -> "leggings";
            case FEET -> "boots";
            case MAINHAND, OFFHAND -> null;
        };
        if (armourType == null) {
            AllTheTrims.LOGGER.error("Item " + itemID + " is marked as trimmable but is not an armour item");
            return;
        }

        String overrideResourceString;
        if (equipment instanceof DyeableArmorItem) {
            overrideResourceString = """
                    {
                       "parent": "minecraft:item/generated",
                       "textures": {
                         "layer0": "%1$s",
                         "layer1": "%1$s_overlay",
                         "layer2": "minecraft:trims/items/%2$s_trim_0_%3$s",
                         "layer3": "minecraft:trims/items/%2$s_trim_1_%3$s",
                         "layer4": "minecraft:trims/items/%2$s_trim_2_%3$s",
                         "layer5": "minecraft:trims/items/%2$s_trim_3_%3$s",
                         "layer6": "minecraft:trims/items/%2$s_trim_4_%3$s",
                         "layer7": "minecraft:trims/items/%2$s_trim_5_%3$s",
                         "layer8": "minecraft:trims/items/%2$s_trim_6_%3$s",
                         "layer9": "minecraft:trims/items/%2$s_trim_7_%3$s"
                       }
                    }
                    """.formatted(itemID.withPrefixedPath("item/"), armourType, AllTheTrims.TRIM_ASSET_NAME);
        } else {
            overrideResourceString = """
                    {
                      "parent": "minecraft:item/generated",
                      "textures": {
                        "layer0": "%1$s",
                        "layer1": "minecraft:trims/items/%2$s_trim_0_%3$s",
                        "layer2": "minecraft:trims/items/%2$s_trim_1_%3$s",
                        "layer3": "minecraft:trims/items/%2$s_trim_2_%3$s",
                        "layer4": "minecraft:trims/items/%2$s_trim_3_%3$s",
                        "layer5": "minecraft:trims/items/%2$s_trim_4_%3$s",
                        "layer6": "minecraft:trims/items/%2$s_trim_5_%3$s",
                        "layer7": "minecraft:trims/items/%2$s_trim_6_%3$s",
                        "layer8": "minecraft:trims/items/%2$s_trim_7_%3$s"
                      }
                    }
                    """.formatted(itemID.withPrefixedPath("item/"), armourType, AllTheTrims.TRIM_ASSET_NAME);
        }
        DebugHelper.createDebugFile("bclib", overrideId.get().getNamespace() + ":" + overrideId.get()
                                                                                               .getPath()
                                                                                               .substring(5) + ".json", overrideResourceString);
        models.put(overrideId.get(), JsonUnbakedModel.deserialize(overrideResourceString));
    }
}
