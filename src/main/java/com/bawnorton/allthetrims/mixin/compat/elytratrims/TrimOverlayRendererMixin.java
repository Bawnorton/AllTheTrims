package com.bawnorton.allthetrims.mixin.compat.elytratrims;

/*? if fabric {*/

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import com.bawnorton.allthetrims.client.render.TrimRenderer;
import com.bawnorton.allthetrims.client.compat.Compat;
import com.bawnorton.allthetrims.util.mixin.ConditionalMixin;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.kikugie.elytratrims.client.render.TrimOverlayRenderer;
import dev.kikugie.elytratrims.client.resource.ETAtlasHolder;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings("UnusedMixin")
@ConditionalMixin("elytratrims")
@Mixin(TrimOverlayRenderer.class)
public abstract class TrimOverlayRendererMixin {
    @Unique
    private static final ThreadLocal<Sprite> allthetrims$SPRITE_CAPTURE = new ThreadLocal<>();

    @WrapOperation(
            method = "render(Lnet/minecraft/client/model/Model;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/kikugie/elytratrims/client/resource/ImageUtilsKt;getMissing(Lnet/minecraft/client/texture/Sprite;)Z"
            )
    )
    private boolean orIsDynamic(Sprite sprite, Operation<Boolean> original, @Local(argsOnly = true) LivingEntity entity, @Local(argsOnly = true) ItemStack stack) {
        AllTheTrimsClient.getShaderManger().setContext(entity, stack.getItem());
        allthetrims$SPRITE_CAPTURE.set(sprite);
        return original.call(sprite) || AllTheTrimsClient.getTrimRenderer().isSpriteDynamic(sprite);
    }

    /**
     * @author Bawnorton
     * @reason ET expects ATT 3.x, 4.x is not compatible. Will pr later
     */
    @SuppressWarnings("FinalPrivateMethod") // kotlin
    @Overwrite
    private final void renderTrimExtended(Model model, MatrixStack matrices, VertexConsumerProvider provider, LivingEntity entity, ItemStack stack, ArmorTrim trim, int light, int colour) {
        Sprite sprite = allthetrims$SPRITE_CAPTURE.get();

        TrimRenderer renderer = AllTheTrimsClient.getTrimRenderer();
        Identifier modelId;
        if (AllTheTrimsClient.getConfig().overrideExisting) {
            modelId = Identifier.ofVanilla("trims/models/elytra/%s_%s".formatted(
                    trim.getPattern().value().assetId().getPath(),
                    AllTheTrims.DYNAMIC
            ));
        } else {
            modelId = renderer.getModelId(sprite);
        }

        RenderLayer renderLayer;
        if (renderer.useLegacyRenderer(sprite)) {
            renderLayer = Compat.getElytraTrimsCompat().orElseThrow().getElytraTrimRenderLayer();
        } else {
            renderLayer = AllTheTrimsClient.getTrimRenderLayer(stack.getItem(), trim);
        }

        renderer.renderTrim(trim, sprite, matrices, provider, light, OverlayTexture.DEFAULT_UV, colour, modelId, ETAtlasHolder.INSTANCE.getAtlas(), renderLayer, model::render);
    }
}
/*?}*/