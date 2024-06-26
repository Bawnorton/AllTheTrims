package com.bawnorton.allthetrims.client.colour;

import com.bawnorton.allthetrims.client.config.Config;
import java.util.ArrayList;
import java.util.List;

public final class ColourInterpolation {
    public static List<Integer> interpolateColors(List<Integer> colors, Config.Interoplation interoplation) {
        if(interoplation == Config.Interoplation.NONE) return colors;

        List<Integer> interpolatedColors = new ArrayList<>();
        for (int i = 0; i < colors.size() - 1; i++) {
            for(int j = 0; j <= 1; j++) {
                float mu = j / 2f;
                switch (interoplation) {
                    case LINEAR -> interpolatedColors.add(linearInterpolate(colors.get(i), colors.get(i + 1), mu));
                    case COSINE -> interpolatedColors.add(cosineInterpolate(colors.get(i), colors.get(i + 1)));
                    case CUBIC -> {
                        int prevColor = i > 0 ? colors.get(i - 1) : colors.get(i);
                        int nextColor = i < colors.size() - 2 ? colors.get(i + 2) : colors.get(i + 1);
                        interpolatedColors.add(cubicInterpolate(prevColor, colors.get(i), colors.get(i + 1), nextColor, mu));
                    }
                }
            }
        }
        interpolatedColors.add(colors.getLast());
        return interpolatedColors;
    }

    private static int linearInterpolate(int color1, int color2, double mu) {
        int r = (int) ((1 - mu) * ((color1 >> 16) & 0xFF) + mu * ((color2 >> 16) & 0xFF));
        int g = (int) ((1 - mu) * ((color1 >> 8) & 0xFF) + mu * ((color2 >> 8) & 0xFF));
        int b = (int) ((1 - mu) * (color1 & 0xFF) + mu * (color2 & 0xFF));
        return (r << 16) | (g << 8) | b;
    }

    private static int cosineInterpolate(int color1, int color2) {
        float mu = (1 - (float) Math.cos(Math.PI)) / 2;
        return linearInterpolate(color1, color2, mu);
    }

    private static int cubicInterpolate(int color0, int color1, int color2, int color3, float mu) {
        int r = cubicInterpolateColour((color0 >> 16) & 0xFF, (color1 >> 16) & 0xFF, (color2 >> 16) & 0xFF, (color3 >> 16) & 0xFF, mu);
        int g = cubicInterpolateColour((color0 >> 8) & 0xFF, (color1 >> 8) & 0xFF, (color2 >> 8) & 0xFF, (color3 >> 8) & 0xFF, mu);
        int b = cubicInterpolateColour(color0 & 0xFF, color1 & 0xFF, color2 & 0xFF, color3 & 0xFF, mu);
        return (r << 16) | (g << 8) | b;
    }

    private static int cubicInterpolateColour(float y0, float y1, float y2, float y3, float mu) {
        float a0, a1, a2, a3, mu2;

        mu2 = mu * mu;
        a0 = y3 - y2 - y0 + y1;
        a1 = y0 - y1 - a0;
        a2 = y2 - y0;
        a3 = y1;

        return (int) (a0 * mu * mu2 + a1 * mu2 + a2 * mu + a3);
    }
}
