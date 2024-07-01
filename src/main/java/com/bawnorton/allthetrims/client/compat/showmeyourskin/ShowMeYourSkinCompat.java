package com.bawnorton.allthetrims.client.compat.showmeyourskin;

//? if fabric {

/*import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.entity.Entity;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.util.math.ColorHelper;
import nl.enjarai.showmeyourskin.client.ModRenderLayers;
import nl.enjarai.showmeyourskin.config.ModConfig;

public final class ShowMeYourSkinCompat {
    public RenderLayer getRenderLayer() {
        return ModRenderLayers.ARMOR_TRANSLUCENT_NO_CULL.apply(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE);
    }

    public int getAlpha(Entity entity, Item trimmed) {
        if(!(trimmed instanceof Equipment equipment)) return 255;

        float alpha = ModConfig.INSTANCE.getApplicableTrimTransparency(entity.getUuid(), equipment.getSlotType());
        return ColorHelper.channelFromFloat(alpha);
    }
}
*///?} else {
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;

public final class ShowMeYourSkinCompat {
    public RenderLayer getRenderLayer() {
        return TexturedRenderLayers.getArmorTrims(false);
    }

    public int getAlpha(Entity entity, Item trimmed) {
        return 255;
    }
}
//?}
