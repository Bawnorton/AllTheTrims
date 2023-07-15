package com.bawnorton.allthetrims.mixin.client;

import com.bawnorton.allthetrims.client.extend.InlinedConditionExtender;
import com.bawnorton.allthetrims.client.extend.ModelOverrideConditionExtender;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelOverrideList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.bawnorton.allthetrims.client.AllTheTrimsClient.MATERIAL;

@Mixin(ModelOverrideList.class)
public abstract class ModelOverrideListMixin {
    @Inject(method = "method_33696", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/json/ModelOverrideList$InlinedCondition;<init>(IF)V"))
    private static void captureMaterial(Object2IntMap<?> map, ModelOverride.Condition condition, CallbackInfoReturnable<?> cir) {
        MATERIAL.set(((ModelOverrideConditionExtender) condition).allTheTrims$getMaterial());
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
