package com.bawnorton.allthetrims.mixin.client;

import com.bawnorton.allthetrims.client.extend.InlinedConditionExtender;
import com.bawnorton.allthetrims.client.extend.ModelOverrideConditionExtender;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

import static com.bawnorton.allthetrims.client.AllTheTrimsClient.MATERIAL;

@Mixin(ModelOverrideList.class)
public abstract class ModelOverrideListMixin {
    @Shadow @Final private ModelOverrideList.BakedOverride[] overrides;

    @Inject(method = "method_33696", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/json/ModelOverrideList$InlinedCondition;<init>(IF)V"))
    private static void captureMaterial(Object2IntMap<?> map, ModelOverride.Condition condition, CallbackInfoReturnable<?> cir) {
        MATERIAL.set(((ModelOverrideConditionExtender) condition).allTheTrims$getMaterial());
    }

    @ModifyExpressionValue(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/json/ModelOverrideList$BakedOverride;test([F)Z"))
    private boolean matchCustomPredicate(boolean original, BakedModel model, ItemStack stack, ClientWorld world, LivingEntity entity, int seed, @Local(name = "bakedOverride") ModelOverrideList.BakedOverride bakedOverride, @Local(name = "fs") float[] fs) {
        if(!(stack.getItem() instanceof ArmorItem)) return original;

        Optional<ArmorTrim> optionalTrim = ArmorTrim.getTrim(world.getRegistryManager(), stack);
        if (optionalTrim.isEmpty()) return original;

        ArmorTrim trim = optionalTrim.get();
        String assetName = trim.getMaterial().value().assetName();

        ModelOverrideList.InlinedCondition[] conditions = bakedOverride.conditions;
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
            MATERIAL.set(null);
        }

        @Override
        public String allTheTrims$getMaterial() {
            return allTheTrims$material;
        }
    }
}
