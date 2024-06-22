package com.bawnorton.allthetrims.client.mixin.compat.elytratrims;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import com.bawnorton.allthetrims.client.api.DynamicTrimRenderer;
import com.bawnorton.allthetrims.client.compat.Compat;
import com.bawnorton.allthetrims.client.compat.elytratrims.ElytraTrimsCompat;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.kikugie.elytratrims.client.render.TrimOverlayRenderer;
import dev.kikugie.elytratrims.client.resource.ETAtlasHolder;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(TrimOverlayRenderer.class)
public abstract class TrimOverlayRendererMixin {
    @Unique
    private static final ThreadLocal<Sprite> allthetrims$SPRITE_CAPTURE = new ThreadLocal<>();

    @Shadow
    public abstract VertexConsumer createVertexConsumer(@NotNull Sprite sprite, @NotNull VertexConsumerProvider provider, @NotNull ItemStack stack);

    @WrapOperation(
            method = "render(Lnet/minecraft/client/model/Model;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/kikugie/elytratrims/client/resource/ImageUtilsKt;getMissing(Lnet/minecraft/client/texture/Sprite;)Z"
            )
    )
    private boolean orIsDynamic(Sprite sprite, Operation<Boolean> original) {
        allthetrims$SPRITE_CAPTURE.set(sprite);
        return original.call(sprite) || AllTheTrimsClient.getTrimRenderer().isSpriteDynamic(sprite);
    }

    /**
     * @author Bawnorton
     * @reason ET expects ATT 3.x, 4.x is not compatible. Will pr later
     */
    @SuppressWarnings("FinalPrivateMethod") // kotlin
    @Overwrite
    private final void renderTrimExtended(Model model, MatrixStack matrices, VertexConsumerProvider provider, LivingEntity entity, ItemStack stack, ArmorTrim trim, int light, int color) {
        Sprite sprite = allthetrims$SPRITE_CAPTURE.get();
        ElytraTrimsCompat compat = Compat.getElytraTrimsCompat().orElseThrow();
        DynamicTrimRenderer renderer = AllTheTrimsClient.getTrimRenderer();
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
            renderLayer = compat.getElytraTrimRenderLayer();
        } else {
            renderLayer = compat.getDynamicElytraTrimRenderLayer(
                    ETAtlasHolder.INSTANCE.getId(),
                    AllTheTrimsClient.getTrimPalettes().getTrimPaletteFor(trim.getMaterial().value().ingredient().value())
            );
        }

        VertexConsumer vertexConsumer = createVertexConsumer(sprite, provider, stack);
        renderer.renderTrim(trim, sprite, model, matrices, vertexConsumer, provider, light, OverlayTexture.DEFAULT_UV, modelId, ETAtlasHolder.INSTANCE.getAtlas(), renderLayer, Model::render);
    }
}
