package com.bawnorton.allthetrims.mixin.client;

import com.bawnorton.allthetrims.Compat;
import com.bawnorton.allthetrims.annotation.ConditionalMixin;
import com.bawnorton.allthetrims.client.extend.InlinedConditionExtender;
import com.bawnorton.allthetrims.client.extend.ModelOverrideConditionExtender;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

import static com.bawnorton.allthetrims.client.AllTheTrimsClient.MATERIAL;

@Mixin(ModelOverrideList.class)
public abstract class ModelOverrideListMixin {
    @Unique
    private static ModelOverrideList.BakedOverride allTheTrims$bakedOverride = null;

    @Inject(method = "method_33696", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/json/ModelOverrideList$InlinedCondition;<init>(IF)V"))
    private static void captureMaterial(Object2IntMap<?> map, ModelOverride.Condition condition, CallbackInfoReturnable<?> cir) {
        MATERIAL.set(((ModelOverrideConditionExtender) condition).allTheTrims$getMaterial());
    }

    @ModifyVariable(method = "apply", at = @At("LOAD"))
    private ModelOverrideList.BakedOverride captureBakedOverride(ModelOverrideList.BakedOverride value) {
        allTheTrims$bakedOverride = value;
        return value;
    }

    @ModifyExpressionValue(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/json/ModelOverrideList$BakedOverride;test([F)Z"))
    private boolean matchCustomPredicate(boolean original, BakedModel model, ItemStack stack, ClientWorld world, LivingEntity entity, int seed) {
        if(Compat.isDynamicTrimLoaded()) return original;
        if(!(stack.getItem() instanceof ArmorItem)) return original;
        if(world == null) return original;

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

    @Mixin(ModelOverrideList.InlinedCondition.class)
    abstract static class InlinedConditionMixin implements InlinedConditionExtender {
        @Unique
        private String allTheTrims$material;

        @Inject(method = "<init>", at = @At("RETURN"))
        private void setMaterial(CallbackInfo ci) {
            this.allTheTrims$material = MATERIAL.get();
            MATERIAL.remove();
        }

        @Override
        public String allTheTrims$getMaterial() {
            return allTheTrims$material;
        }
    }
}
