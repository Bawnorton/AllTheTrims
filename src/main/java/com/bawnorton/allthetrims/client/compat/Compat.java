package com.bawnorton.allthetrims.client.compat;

import com.bawnorton.allthetrims.client.compat.elytratrims.ElytraTrimsCompat;
import com.bawnorton.allthetrims.client.compat.iris.IrisCompat;
import com.bawnorton.allthetrims.client.compat.yacl.YACLImpl;
import com.bawnorton.allthetrims.platform.Platform;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import java.util.Optional;

public final class Compat {
    private static ElytraTrimsCompat elytraTrimsCompat;
    private static IrisCompat irisCompat;
    private static YACLImpl yaclImp;

    public static Optional<IrisCompat> getIrisCompat() {
        if(!Platform.isModLoaded("iris")) return Optional.empty();

        if(irisCompat == null) irisCompat = new IrisCompat();
        return Optional.of(irisCompat);
    }

    public static Optional<YACLImpl> getYaclImpl() {
        if (!Platform.isModLoaded("yet_another_config_lib_v3")) return Optional.empty();

        if (yaclImp == null) yaclImp = new YACLImpl();
        return Optional.of(yaclImp);
    }

    public static Optional<ElytraTrimsCompat> getElytraTrimsCompat() {
        if (!Platform.isModLoaded("elytratrims")) return Optional.empty();

        if(elytraTrimsCompat == null) elytraTrimsCompat = new ElytraTrimsCompat();
        return Optional.of(elytraTrimsCompat);
    }

    public static RenderLayer getTrimRenderLayer(boolean decal) {
        return TexturedRenderLayers.getArmorTrims(decal);
    }
}
