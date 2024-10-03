package com.bawnorton.allthetrims.mixin.compat.fabric.mythicmetals;

//? if fabric {

/*import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import com.bawnorton.allthetrims.client.colour.ARGBColourHelper;
import com.bawnorton.allthetrims.client.render.TrimRenderer;
import com.bawnorton.allthetrims.util.mixin.ConditionalMixin;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.util.math.ColorHelper;
import nourl.mythicmetals.MythicMetalsClient;
import nourl.mythicmetals.armor.HallowedArmor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings("UnusedMixin")
@ConditionalMixin("mythicmetals")
@Mixin(MythicMetalsClient.class)
public abstract class MythicMetalsClientMixin {
    //? if >1.20.6 {
    @WrapOperation(
            method = "lambda$registerArmorRenderer$9",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"
            )
    )
    private static void renderDynamicTrim(BipedEntityModel<?> instance, MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay, Operation<Void> original,
            @Local(argsOnly = true) VertexConsumerProvider provider,
            @Local ArmorTrim trim,
            @Local Sprite sprite,
            @Local HallowedArmor armour) {
    //?} else {
    /^@WrapOperation(
            method = "lambda$registerArmorRenderer$11",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"
            )
    )
    private static void renderDynamicTrim(BipedEntityModel<?> instance, MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha, Operation<Void> original,
            @Local(argsOnly = true) VertexConsumerProvider provider,
            @Local(argsOnly = true) ArmorTrim trim,
            @Local Sprite sprite,
            @Local(argsOnly = true) HallowedArmor armour) {
    ^///?}
        AllTheTrimsClient.getTrimRenderer().renderTrim(
                trim,
                armour.getMaterial(),
                armour.getSlotType() == EquipmentSlot.LEGS,
                sprite,
                matrixStack,
                provider,
                light,
                overlay,
                -1,
                MinecraftClient.getInstance().getBakedModelManager().getAtlas(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE),
                //? if >1.20.6 {
                instance::render
                 //?} else {
                /^(matrices, vertices, light1, overlay1, colour) -> {
                    float r = ARGBColourHelper.floatFromChannel(ColorHelper.Argb.getRed(colour));
                    float g = ARGBColourHelper.floatFromChannel(ColorHelper.Argb.getGreen(colour));
                    float b = ARGBColourHelper.floatFromChannel(ColorHelper.Argb.getBlue(colour));
                    float a = ARGBColourHelper.floatFromChannel(ColorHelper.Argb.getAlpha(colour));
                    original.call(instance, matrices, vertices, light1, overlay1, r, g, b, a);
                }
                ^///?}
        );
    }
}

*///?}