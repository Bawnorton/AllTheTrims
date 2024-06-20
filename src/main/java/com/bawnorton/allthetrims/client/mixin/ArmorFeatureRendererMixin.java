package com.bawnorton.allthetrims.client.mixin;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.client.api.DynamicTrimRenderer;
import com.bawnorton.allthetrims.client.compat.Compat;
import com.bawnorton.allthetrims.client.compat.iris.IrisCompat;
import com.bawnorton.allthetrims.client.palette.TrimPalette;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity> {
    @Shadow @Final private SpriteAtlasTexture armorTrimsAtlas;

    @ModifyExpressionValue(
            method = "renderTrim",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/texture/SpriteAtlasTexture;getSprite(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/texture/Sprite;"
            )
    )
    private Sprite captureSprite(Sprite original, @Share("sprite") LocalRef<Sprite> spriteLocalRef) {
        boolean doCapture = original.getContents().getId().getPath().endsWith("_dynamic");
        spriteLocalRef.set(doCapture ? original : null);
        return original;
    }

    @WrapOperation(
            method = "renderTrim",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"
            )
    )
    private void renderDynamicTrim(BipedEntityModel<T> instance, MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int uv, Operation<Void> original, RegistryEntry<ArmorMaterial> armorMaterial, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int lightArg, ArmorTrim trim, BipedEntityModel<T> model, boolean leggings, @Share("sprite") LocalRef<Sprite> spriteLocalRef) {
        if(AllTheTrims.getConfig().animate) {
            AllTheTrims.getTrimPalettes().forEach(TrimPalette::cycleAnimatedColours);
        }

        Sprite sprite = spriteLocalRef.get();
        boolean useLegacyRenderer = AllTheTrims.getConfig().useLegacyRenderer && sprite != null; // specified to use it
        useLegacyRenderer |= Compat.isIrisLoaded() && IrisCompat.isUsingShader(); // have to use it
        useLegacyRenderer |= AllTheTrims.getConfig().overrideExisting && sprite == null; // overriding existing
        if (useLegacyRenderer) {
            DynamicTrimRenderer.setAtlas(armorTrimsAtlas);
            DynamicTrimRenderer.renderTrim(armorMaterial, matrices, vertexConsumers, light, trim, model, leggings);
            return;
        }

        if(sprite == null) { // sprite is not dynamic
            original.call(instance, matrixStack, vertexConsumer, light, uv);
            return;
        }

        Item material = trim.getMaterial().value().ingredient().value();
        TrimPalette palette = AllTheTrims.getTrimPalettes().getTrimPaletteFor(material);
        RenderLayer dynamicTrimRenderLayer = AllTheTrims.getShaderManager().getDynamicTrimRenderLayer(palette);
        VertexConsumer vertices = sprite.getTextureSpecificVertexConsumer(vertexConsumers.getBuffer(dynamicTrimRenderLayer));
        original.call(instance, matrixStack, vertices, light, uv);
    }
}
