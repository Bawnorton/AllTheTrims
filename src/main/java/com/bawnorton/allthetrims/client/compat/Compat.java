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
        return Platform.isModLoaded("iris") ? Optional.of(irisCompat == null ? irisCompat = new IrisCompat() : irisCompat) : Optional.empty();
    }

    public static Optional<ElytraTrimsCompat> getElytraTrimsCompat() {
        return Platform.isModLoaded("elytratrims") ? Optional.of(elytraTrimsCompat == null ? elytraTrimsCompat = new ElytraTrimsCompat() : elytraTrimsCompat) : Optional.empty();
    }

    public static Optional<ShowMeYourSkinCompat> getShowMeYourSkinCompat() {
        return Platform.isModLoaded("showmeyourskin") ? Optional.of(showMeYourSkinCompat == null ? showMeYourSkinCompat = new ShowMeYourSkinCompat() : showMeYourSkinCompat) : Optional.empty();
    }

    public static Optional<YACLImpl> getYaclImpl() {
        return Platform.isModLoaded("yet_another_config_lib_v3") ? Optional.of(yaclImpl == null ? yaclImpl = new YACLImpl() : yaclImpl) : Optional.empty();
    }
}
