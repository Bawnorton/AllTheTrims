package com.bawnorton.allthetrims.client.colour;

import net.minecraft.util.math.MathHelper;

public final class ARGBColourHelper {
    public static int fullAlpha(int colour) {
        return colour | 0xFF000000;
    }

    public static int withAlpha(int colour, int alpha) {
        return alpha << 24 | colour & 16777215;
    }

    public static int channelFromFloat(float alpha) {
        return MathHelper.floor(alpha * 255f);
    }

    public static float floatFromChannel(int channel) {
        return channel / 255f;
    }
}
