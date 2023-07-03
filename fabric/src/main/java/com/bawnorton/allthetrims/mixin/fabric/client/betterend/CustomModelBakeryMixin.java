package com.bawnorton.allthetrims.mixin.fabric.client.betterend;

import com.bawnorton.allthetrims.AllTheTrims;
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
public abstract class CustomModelBakeryMixin {
    @Shadow @Final private Map<Identifier, UnbakedModel> models;

    @Inject(method = "addItemModel", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private void modifyItemModels(Identifier itemID, ItemModelProvider provider, CallbackInfo ci, @Local(name = "model") JsonUnbakedModel model) {
        AtomicBoolean isTrimmable = new AtomicBoolean(false);
        AtomicReference<Identifier> overrideId = new AtomicReference<>();
        model.getOverrides().forEach(modelOverride -> modelOverride.streamConditions().forEach(condition -> {
            if(condition.getType().equals(new Identifier("trim_type")) && condition.getThreshold() == 0.099f) {
                isTrimmable.set(true);
                overrideId.set(modelOverride.getModelId());
            }
        }));
        if(isTrimmable.get()) {
            if(overrideId.get() == null) {
                AllTheTrims.LOGGER.error("Item " + itemID + " is marked as trimmable but has no model override id");
                return;
            }

            Item item = Registries.ITEM.get(itemID);
            if(!(item instanceof Equipment equipment)) {
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
            if(armourType == null) {
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
                                 "layer2": "minecraft:trims/items/%2$s_trim_0_att-blank",
                                 "layer3": "minecraft:trims/items/%2$s_trim_1_att-blank",
                                 "layer4": "minecraft:trims/items/%2$s_trim_2_att-blank",
                                 "layer5": "minecraft:trims/items/%2$s_trim_3_att-blank",
                                 "layer6": "minecraft:trims/items/%2$s_trim_4_att-blank",
                                 "layer7": "minecraft:trims/items/%2$s_trim_5_att-blank",
                                 "layer8": "minecraft:trims/items/%2$s_trim_6_att-blank",
                                 "layer9": "minecraft:trims/items/%2$s_trim_7_att-blank"
                               }
                            }
                            """.formatted(itemID.withPrefixedPath("item/"), armourType);
            } else {
                overrideResourceString = """
                            {
                              "parent": "minecraft:item/generated",
                              "textures": {
                                "layer0": "%1$s",
                                "layer1": "minecraft:trims/items/%2$s_trim_0_att-blank",
                                "layer2": "minecraft:trims/items/%2$s_trim_1_att-blank",
                                "layer3": "minecraft:trims/items/%2$s_trim_2_att-blank",
                                "layer4": "minecraft:trims/items/%2$s_trim_3_att-blank",
                                "layer5": "minecraft:trims/items/%2$s_trim_4_att-blank",
                                "layer6": "minecraft:trims/items/%2$s_trim_5_att-blank",
                                "layer7": "minecraft:trims/items/%2$s_trim_6_att-blank",
                                "layer8": "minecraft:trims/items/%2$s_trim_7_att-blank"
                              }
                            }
                            """.formatted(itemID.withPrefixedPath("item/"), armourType);
            }
            models.put(overrideId.get(), JsonUnbakedModel.deserialize(overrideResourceString));
        }
    }
}
