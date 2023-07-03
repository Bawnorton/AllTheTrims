package com.bawnorton.allthetrims.fabric.client.compat;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import nl.enjarai.showmeyourskin.client.ModRenderLayers;
import nl.enjarai.showmeyourskin.util.ArmorContext;
import nl.enjarai.showmeyourskin.util.MixinContext;

public abstract class ShowMeYourSkinCompat {
    public static float getTrimTransparency() {
        ArmorContext context = MixinContext.ARMOR.getContext();
        if(context != null) {
            return context.getApplicableTrimTransparency();
        } else {
            return 1.0F;
        }
    }

    public static RenderLayer getTrimRenderLayer() {
        return ModRenderLayers.ARMOR_TRANSLUCENT_NO_CULL.apply(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE);
    }
}
