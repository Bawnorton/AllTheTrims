package com.bawnorton.allthetrims.client.compat;

import com.bawnorton.allthetrims.client.compat.elytratrims.ElytraTrimsCompat;
import com.bawnorton.allthetrims.client.compat.iris.IrisCompat;
import com.bawnorton.allthetrims.client.compat.showmeyourskin.ShowMeYourSkinCompat;
import com.bawnorton.allthetrims.client.compat.yacl.YACLImpl;
import java.util.Optional;

public final class Compat {
    private static final CompatEntry<IrisCompat> irisCompat = CompatEntry.of("iris", IrisCompat::new);
    private static final CompatEntry<ElytraTrimsCompat> elytraTrimsCompat = CompatEntry.of("elytratrims", ElytraTrimsCompat::new);
    private static final CompatEntry<ShowMeYourSkinCompat> showMeYourSkinCompat = CompatEntry.of("showmeyourskin", ShowMeYourSkinCompat::new);
    private static final CompatEntry<YACLImpl> yaclImpl = CompatEntry.of("yet_another_config_lib_v3", YACLImpl::new);

    public static Optional<IrisCompat> getIrisCompat() {
        return irisCompat.getCompat();
    }

    public static Optional<ElytraTrimsCompat> getElytraTrimsCompat() {
        return elytraTrimsCompat.getCompat();
    }

    public static Optional<ShowMeYourSkinCompat> getShowMeYourSkinCompat() {
        return showMeYourSkinCompat.getCompat();
    }

    public static Optional<YACLImpl> getYaclImpl() {
        return yaclImpl.getCompat();
    }
}
