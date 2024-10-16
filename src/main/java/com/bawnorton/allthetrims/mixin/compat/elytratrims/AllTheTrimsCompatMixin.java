package com.bawnorton.allthetrims.mixin.compat.elytratrims;

import com.bawnorton.runtimetrims.RuntimeTrims;
import com.bawnorton.runtimetrims.client.RuntimeTrimsClient;
import com.bawnorton.runtimetrims.client.render.TrimRenderer;
import com.llamalad7.mixinextras.sugar.Cancellable;
import dev.kikugie.elytratrims.client.render.TrimOverlayRenderer;
import dev.kikugie.elytratrims.client.resource.ETAtlasHolder;
import dev.kikugie.elytratrims.common.compat.AllTheTrimsCompat;
import kotlin.jvm.functions.Function1;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TrimOverlayRenderer.class)
public abstract class AllTheTrimsCompatMixin {
    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/kikugie/elytratrims/common/compat/AllTheTrimsCompat;renderTrimAtt(Lnet/minecraft/client/texture/Sprite;Lnet/minecraft/client/model/Model;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/trim/ArmorTrim;IILkotlin/jvm/functions/Function1;)V"
            )
    )
    private void renderUsingRuntimeTrims(AllTheTrimsCompat instance, Sprite sprite, Model model, MatrixStack matrices, VertexConsumerProvider provider, LivingEntity entity, ItemStack stack, ArmorTrim trim, int light, int colour, Function1<? super TrimOverlayRenderer.TrimInfo, ? extends Sprite> func, @Cancellable CallbackInfo ci) {
        TrimRenderer renderer = RuntimeTrimsClient.getTrimRenderer();
        renderer.setContext(entity, stack.getItem());
        Identifier modelId;
        if(RuntimeTrimsClient.overrideExisting) {
            modelId = Identifier.ofVanilla(
                    "trims/models/elytra/%s_%s".formatted(
                            trim.getPattern().value().assetId().getPath(),
                            RuntimeTrims.DYNAMIC
                    )
            );
        } else {
            modelId = renderer.getModelId(sprite);
        }

        renderer.renderTrim(
                trim,
                sprite,
                matrices,
                provider,
                light,
                OverlayTexture.DEFAULT_UV,
                colour,
                modelId,
                ETAtlasHolder.INSTANCE.getAtlas(),
                model::render
        );
        ci.cancel();
    }
}
