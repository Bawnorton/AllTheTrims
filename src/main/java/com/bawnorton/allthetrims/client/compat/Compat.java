package com.bawnorton.allthetrims.client.compat;

import com.bawnorton.allthetrims.client.compat.elytratrims.ElytraTrimsCompat;
import com.bawnorton.allthetrims.client.compat.iris.IrisCompat;
import com.bawnorton.allthetrims.client.compat.showmeyourskin.ShowMeYourSkinCompat;
import com.bawnorton.allthetrims.client.compat.yacl.YACLImpl;
import com.bawnorton.allthetrims.platform.Platform;
import java.util.Optional;

public final class Compat {
    private static IrisCompat irisCompat;
    private static ElytraTrimsCompat elytraTrimsCompat;
    private static ShowMeYourSkinCompat showMeYourSkinCompat;
    private static YACLImpl yaclImpl;

    public static Optional<IrisCompat> getIrisCompat() {
        if (!Platform.isModLoaded("iris")) return Optional.empty();
        if (irisCompat == null) irisCompat = new IrisCompat();

        return Optional.of(irisCompat);
    }

    public static Optional<ElytraTrimsCompat> getElytraTrimsCompat() {
        if (!Platform.isModLoaded("elytratrims")) return Optional.empty();
        if (elytraTrimsCompat == null) elytraTrimsCompat = new ElytraTrimsCompat();

        return Optional.of(elytraTrimsCompat);
    }

    public static Optional<ShowMeYourSkinCompat> getShowMeYourSkinCompat() {
        if (!Platform.isModLoaded("showmeyourskin")) return Optional.empty();
        if (showMeYourSkinCompat == null) showMeYourSkinCompat = new ShowMeYourSkinCompat();

        return Optional.of(showMeYourSkinCompat);
    }

    public static Optional<YACLImpl> getYaclImpl() {
        if (!Platform.isModLoaded("yet_another_config_lib_v3")) return Optional.empty();
        if (yaclImpl == null) yaclImpl = new YACLImpl();

        return Optional.of(yaclImpl);
    }
}
