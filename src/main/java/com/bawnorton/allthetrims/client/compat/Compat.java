package com.bawnorton.allthetrims.client.compat;

import com.bawnorton.allthetrims.client.compat.elytratrims.ElytraTrimsCompat;
import com.bawnorton.allthetrims.platform.Platform;
import java.util.Optional;

public final class Compat {
    private static ElytraTrimsCompat elytraTrimsCompat;

    public static Optional<ElytraTrimsCompat> getElytraTrimsCompat() {
        if (!Platform.isModLoaded("elytratrims")) return Optional.empty();
        if (elytraTrimsCompat == null) elytraTrimsCompat = new ElytraTrimsCompat();

        return Optional.of(elytraTrimsCompat);
    }

}
