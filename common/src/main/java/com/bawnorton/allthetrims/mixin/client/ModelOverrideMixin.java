package com.bawnorton.allthetrims.mixin.client;

import com.bawnorton.allthetrims.client.extend.ModelOverrideConditionExtender;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.bawnorton.allthetrims.client.AllTheTrimsClient.MATERIAL;

@Mixin(ModelOverride.class)
public abstract class ModelOverrideMixin {
    @Mixin(ModelOverride.Condition.class)
    abstract static class ConditionMixin implements ModelOverrideConditionExtender {
        @Unique
        private String allTheTrims$material;

        @Inject(method = "<init>", at = @At("RETURN"))
        private void setMaterial(Identifier type, float threshold, CallbackInfo ci) {
            this.allTheTrims$material = MATERIAL.get();
            MATERIAL.remove();
        }

        @Unique
        public String allTheTrims$getMaterial() {
            return allTheTrims$material;
        }
    }

    @Mixin(ModelOverride.Deserializer.class)
    abstract static class DeserializerMixin {
        @WrapOperation(method = "deserializeMinPropertyValues", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/JsonHelper;asFloat(Lcom/google/gson/JsonElement;Ljava/lang/String;)F"))
        private static float captureMaterial(JsonElement value, String name, Operation<Float> original) {
            try {
                return original.call(value, name);
            } catch (JsonSyntaxException e) {
                if(!name.equals("trim_type")) throw e;
                if(!value.isJsonPrimitive()) throw e;

                JsonPrimitive primitive = value.getAsJsonPrimitive();
                MATERIAL.set(primitive.getAsString());
                return Float.MAX_VALUE;
            }
        }
    }
}

