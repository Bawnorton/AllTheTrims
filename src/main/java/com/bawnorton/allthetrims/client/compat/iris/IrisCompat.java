package com.bawnorton.allthetrims.client.compat.iris;

import net.irisshaders.iris.Iris;

public final class IrisCompat {
    public static boolean isUsingShader() {
        return Iris.getCurrentPack().isPresent();
    }
}
