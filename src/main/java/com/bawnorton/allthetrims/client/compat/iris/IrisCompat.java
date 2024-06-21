package com.bawnorton.allthetrims.client.compat.iris;

public final class IrisCompat {
    public boolean isUsingShader() {
        //? if fabric {
        return net.irisshaders.iris.Iris.getCurrentPack().isPresent();
        //? } else {
        /*return false
        *///? }
    }
}
