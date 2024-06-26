package com.bawnorton.allthetrims.client.compat;

import com.bawnorton.allthetrims.client.compat.elytratrims.ElytraTrimsCompat;
import com.bawnorton.allthetrims.client.compat.iris.IrisCompat;
import com.bawnorton.allthetrims.client.compat.showmeyourskin.ShowMeYourSkinCompat;
import com.bawnorton.allthetrims.client.compat.yacl.YACLImpl;
import com.bawnorton.allthetrims.platform.Platform;
import com.bawnorton.allthetrims.util.LazySupplier;
import java.util.Optional;

public final class Compat {
    private static final LazySupplier<ElytraTrimsCompat> elytraTrimsCompat = LazySupplier.of(ElytraTrimsCompat::new);
    private static final LazySupplier<ShowMeYourSkinCompat> showMeYourSkinCompat = LazySupplier.of(ShowMeYourSkinCompat::new);
    private static final LazySupplier<IrisCompat> irisCompat = LazySupplier.of(IrisCompat::new);
    private static final LazySupplier<YACLImpl> yaclImp = LazySupplier.of(YACLImpl::new);

    public static Optional<IrisCompat> getIrisCompat() {
        if(!Platform.isModLoaded("iris")) return Optional.empty();

        return Optional.of(irisCompat.get());
    }

    public static Optional<YACLImpl> getYaclImpl() {
        if (!Platform.isModLoaded("yet_another_config_lib_v3")) return Optional.empty();

        return Optional.of(yaclImp.get());
    }

    public static Optional<ElytraTrimsCompat> getElytraTrimsCompat() {
        if (!Platform.isModLoaded("elytratrims")) return Optional.empty();

        return Optional.of(elytraTrimsCompat.get());
    }

    public static Optional<ShowMeYourSkinCompat> getShowMeYourSkinCompat() {
        if (!Platform.isModLoaded("showmeyourskin")) return Optional.empty();

        return Optional.of(showMeYourSkinCompat.get());
    }
}
