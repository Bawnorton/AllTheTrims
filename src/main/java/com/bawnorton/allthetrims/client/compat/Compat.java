package com.bawnorton.allthetrims.client.compat;

import com.bawnorton.allthetrims.client.compat.elytratrims.ElytraTrimsCompat;
import com.bawnorton.allthetrims.client.compat.iris.IrisCompat;
import com.bawnorton.allthetrims.client.compat.showmeyourskin.ShowMeYourSkinCompat;
import com.bawnorton.allthetrims.client.compat.yacl.YACLImpl;
import com.bawnorton.allthetrims.util.LazySupplier;
import java.util.Optional;

public final class Compat {
    private static final LazySupplier<CompatEntry<IrisCompat>> irisCompat = LazySupplier.of(() -> CompatEntry.of("iris", new IrisCompat()));
    private static final LazySupplier<CompatEntry<ElytraTrimsCompat>> elytraTrimsCompat = LazySupplier.of(() -> CompatEntry.of("elytratrims", new ElytraTrimsCompat()));
    private static final LazySupplier<CompatEntry<ShowMeYourSkinCompat>> showMeYourSkinCompat = LazySupplier.of(() -> CompatEntry.of("showmeyourskin", new ShowMeYourSkinCompat()));
    private static final LazySupplier<CompatEntry<YACLImpl>> yaclImpl = LazySupplier.of(() -> CompatEntry.of("yet_another_config_lib_v3", new YACLImpl()));

    public static Optional<IrisCompat> getIrisCompat() {
        return irisCompat.get().getCompat();
    }

    public static Optional<ElytraTrimsCompat> getElytraTrimsCompat() {
        return elytraTrimsCompat.get().getCompat();
    }

    public static Optional<ShowMeYourSkinCompat> getShowMeYourSkinCompat() {
        return showMeYourSkinCompat.get().getCompat();
    }

    public static Optional<YACLImpl> getYaclImpl() {
        return yaclImpl.get().getCompat();
    }
}
