package com.bawnorton.allthetrims.mixin.fabric.client.elytratrims;

import com.bawnorton.allthetrims.client.util.PaletteHelper;
import dev.kikugie.elytratrims.config.ConfigState;
import dev.kikugie.elytratrims.render.ExtraElytraFeatureRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;

import java.awt.*;
import java.util.List;
import java.util.function.Function;

@Pseudo
@Mixin(value = ExtraElytraFeatureRenderer.class, remap = false)
public abstract class ExtraElytraFeatureRendererMixin {
    @Shadow @Final private ElytraEntityModel<?> elytra;
    @Shadow @Final private static Function<Identifier, RenderLayer> ELYTRA_LAYER;
    @Shadow @Final private SpriteAtlasTexture atlas;

    /**
     * @author Bawnorton
     * @reason See {@link com.bawnorton.allthetrims.mixin.client.ArmorFeatureRendererMixin}
     */
    @Overwrite
    private void renderElytraTrims(MatrixStack matrices, VertexConsumerProvider provider, LivingEntity entity, ItemStack stack, int light, float alpha) {
        if (!ConfigState.cancelRender(ConfigState.RenderType.TRIMS, entity)) {
            World world = entity.getWorld();
            ArmorTrim trim = ArmorTrim.getTrim(world.getRegistryManager(), stack).orElse(null);
            if (trim != null) {
                List<Color> palette = PaletteHelper.getPalette(trim.getMaterial().value().ingredient().value());
                for(int i = 0; i < 8; i++) {
                    Sprite sprite = getTrimSprite(trim, i);
                    VertexConsumer vertexConsumer = sprite.getTextureSpecificVertexConsumer(ItemRenderer.getDirectItemGlintConsumer(provider, ELYTRA_LAYER.apply(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE), false, stack.hasGlint()));
                    Color colour = palette.get(i);
                    elytra.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, colour.getRed() / 255f, colour.getGreen() / 255f, colour.getBlue() / 255f, 1.0F);
                }
            }
        }
    }

    private Sprite getTrimSprite(ArmorTrim trim, int index) {
        String material = trim.getMaterial().value().assetName();
        Identifier identifier = trim.getPattern().value().assetId().withPath((path) -> "trims/models/elytra/" + path + "_" + index + "_" + material);
        return this.atlas.getSprite(identifier);
    }
}
