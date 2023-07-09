package com.bawnorton.allthetrims.mixin.fabric.client;

import com.bawnorton.allthetrims.client.api.DynamicTrimRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.trim.ArmorTrim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = ArmorFeatureRenderer.class, priority = 1500)
public abstract class ArmorFeatureRendererMixin {
    /**
     * @author Bawnorton
     * @reason Completely replace the renderTrim method to render all the dynamic layers
     */
    @Overwrite
    private void renderTrim(ArmorMaterial material, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorTrim trim, BipedEntityModel<?> model, boolean leggings) {
        DynamicTrimRenderer.renderTrim(material, matrices, vertexConsumers, light, trim, model, leggings);
    }
}
