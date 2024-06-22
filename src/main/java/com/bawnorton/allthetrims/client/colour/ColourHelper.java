package com.bawnorton.allthetrims.client.colour;

public final class ColourHelper {
    public static float[] unpackARGB(int argb) {
        float a = (float) ((argb >> 24) & 0xFF) / 255.0f;
        float r = (float) ((argb >> 16) & 0xFF) / 255.0f;
        float g = (float) ((argb >> 8) & 0xFF) / 255.0f;
        float b = (float) ((argb) & 0xFF) / 255.0f;
        return new float[]{a, r, g, b};
    }
}
