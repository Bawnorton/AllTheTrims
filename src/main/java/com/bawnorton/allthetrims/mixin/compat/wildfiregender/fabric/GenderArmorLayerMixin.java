package com.bawnorton.allthetrims.mixin.compat.wildfiregender.fabric;

//? if fabric {

/*import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import com.bawnorton.allthetrims.client.render.TrimRenderer;
import com.bawnorton.allthetrims.util.mixin.ConditionalMixin;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.wildfire.render.BreastSide;
import com.wildfire.render.GenderArmorLayer;
import com.wildfire.render.GenderLayer;
import com.wildfire.render.WildfireModelRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnusedMixin")
@ConditionalMixin("wildfire_gender")
@Mixin(GenderArmorLayer.class)
public abstract class GenderArmorLayerMixin<T extends LivingEntity, M extends BipedEntityModel<T>> extends GenderLayer<T, M> {
    @Shadow(remap = false) @Final
    protected static WildfireModelRenderer.BreastModelBox lTrim;
    @Shadow(remap = false) @Final
    protected static WildfireModelRenderer.BreastModelBox rTrim;

    @Shadow @Final
    private SpriteAtlasTexture armorTrimsAtlas;

    protected GenderArmorLayerMixin(FeatureRendererContext<T, M> render) {
        super(render);
    }

    @Inject(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/wildfire/render/GenderArmorLayer;renderSides(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/model/BipedEntityModel;Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/function/Consumer;)V"
            )
    )
    private void captureTrimmedItem(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, @NotNull T ent, float limbAngle, float limbDistance, float partialTicks, float animationProgress, float headYaw, float headPitch, CallbackInfo ci, @Local ItemStack stack) {
        AllTheTrimsClient.getShaderManger().setContext(ent, armorStack.getItem());
    }

    @WrapOperation(
            method = "lambda$render$1",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/wildfire/render/GenderArmorLayer;renderArmorTrim(Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/item/trim/ArmorTrim;ZLcom/wildfire/render/BreastSide;)V"
            )
    )
    private void renderDynamicTrim(GenderArmorLayer<T, M> instance, RegistryEntry<ArmorMaterial> material, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int packedLightIn, ArmorTrim trim, boolean hasGlint, BreastSide side, Operation<Void> original) {
        if(!AllTheTrimsClient.isDynamic(trim)) {
            original.call(instance, material, matrixStack, vertexConsumerProvider, packedLightIn, trim, hasGlint, side);
            return;
        }

        Identifier modelId = trim.getGenericModelId(material);
        String assetName = trim.getMaterial().value().assetName();
        if (AllTheTrimsClient.getConfig().overrideExisting && !assetName.equals(AllTheTrims.DYNAMIC)) {
            modelId = modelId.withPath(path -> path.replace(assetName, AllTheTrims.DYNAMIC));
        }

        Sprite sprite = armorTrimsAtlas.getSprite(modelId);
        WildfireModelRenderer.BreastModelBox trimBox = side.isLeft ? lTrim : rTrim;
        TrimRenderer trimRenderer = AllTheTrimsClient.getTrimRenderer();

        trimRenderer.renderTrim(
                trim,
                sprite,
                matrixStack,
                vertexConsumerProvider,
                packedLightIn,
                OverlayTexture.DEFAULT_UV,
                -1,
                armorTrimsAtlas,
                (matrices, vertices, light, overlay, colour) -> renderBox(trimBox, matrices, vertices, light, overlay, colour)
        );

        if(hasGlint) {
            renderBox(trimBox, matrixStack, vertexConsumerProvider.getBuffer(RenderLayer.getArmorEntityGlint()), packedLightIn, OverlayTexture.DEFAULT_UV, -1);
        }
    }
}
*///?}
