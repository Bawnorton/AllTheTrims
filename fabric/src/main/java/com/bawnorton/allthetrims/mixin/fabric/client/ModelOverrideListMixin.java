package com.bawnorton.allthetrims.mixin.fabric.client;

import com.bawnorton.allthetrims.client.extend.InlinedConditionExtender;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Optional;

@Mixin(ModelOverrideList.class)
public abstract class ModelOverrideListMixin {
    @Unique
    private static ModelOverrideList.BakedOverride allTheTrims$bakedOverride = null;

    @SuppressWarnings({"UnresolvedMixinReference", "MixinAnnotationTarget"})
    @ModifyVariable(method = "method_3495", at = @At("LOAD"), remap = false) // [for production] for some reason the remap fails ¯\_(ツ)_/¯
//    @ModifyVariable(method = "apply", at = @At("LOAD"))
    private ModelOverrideList.BakedOverride captureBakedOverride(ModelOverrideList.BakedOverride value) {
        allTheTrims$bakedOverride = value;
        return value;
    }

    @ModifyExpressionValue(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/json/ModelOverrideList$BakedOverride;test([F)Z"))
    private boolean matchCustomPredicate(boolean original, BakedModel model, ItemStack stack, ClientWorld world, LivingEntity entity, int seed) {
        if(!(stack.getItem() instanceof ArmorItem)) return original;

        Optional<ArmorTrim> optionalTrim = ArmorTrim.getTrim(world.getRegistryManager(), stack);
        if (optionalTrim.isEmpty()) return original;

        ArmorTrim trim = optionalTrim.get();
        String assetName = trim.getMaterial().value().assetName();

        ModelOverrideList.InlinedCondition[] conditions = allTheTrims$bakedOverride.conditions;
        for (ModelOverrideList.InlinedCondition condition : conditions) {
            if (!(condition instanceof InlinedConditionExtender extender)) continue;

            String conditionMaterial = extender.allTheTrims$getMaterial();
            if (conditionMaterial == null || !conditionMaterial.equals(assetName)) continue;

            return true;
        }
        return original;
    }
}
