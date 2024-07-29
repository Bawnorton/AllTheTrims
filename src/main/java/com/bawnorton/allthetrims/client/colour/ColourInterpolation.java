package com.bawnorton.allthetrims.client.colour;

import com.bawnorton.allthetrims.client.config.Config;
import com.bawnorton.allthetrims.versioned.VLists;
import java.util.ArrayList;
import java.util.List;

public final class ColourInterpolation {
    public static List<Integer> interpolateColours(List<Integer> colours, Config.Interoplation interoplation) {
        if(interoplation == Config.Interoplation.NONE) return colours;

        List<Integer> interpolatedColours = new ArrayList<>();
        for (int i = 0; i < colours.size() - 1; i++) {
            for(int j = 0; j <= 1; j++) {
                float mu = j / 2f;
                switch (interoplation) {
                    case LINEAR -> interpolatedColours.add(linearInterpolate(colours.get(i), colours.get(i + 1), mu));
                    case COSINE -> interpolatedColours.add(cosineInterpolate(colours.get(i), colours.get(i + 1)));
                    case CUBIC -> {
                        int prevColour = i > 0 ? colours.get(i - 1) : colours.get(i);
                        int nextColour = i < colours.size() - 2 ? colours.get(i + 2) : colours.get(i + 1);
                        interpolatedColours.add(cubicInterpolate(prevColour, colours.get(i), colours.get(i + 1), nextColour, mu));
                    }
                }
            }
        }
        interpolatedColours.add(VLists.getLast(colours));
        return interpolatedColours;
    }

    private static int linearInterpolate(int colour1, int colour2, double mu) {
        int r = (int) ((1 - mu) * ((colour1 >> 16) & 0xFF) + mu * ((colour2 >> 16) & 0xFF));
        int g = (int) ((1 - mu) * ((colour1 >> 8) & 0xFF) + mu * ((colour2 >> 8) & 0xFF));
        int b = (int) ((1 - mu) * (colour1 & 0xFF) + mu * (colour2 & 0xFF));
        return (r << 16) | (g << 8) | b;
    }

    private static int cosineInterpolate(int colour1, int colour2) {
        float mu = (1 - (float) Math.cos(Math.PI)) / 2;
        return linearInterpolate(colour1, colour2, mu);
    }

    private static int cubicInterpolate(int colour0, int colour1, int colour2, int colour3, float mu) {
        int r = cubicInterpolateColour((colour0 >> 16) & 0xFF, (colour1 >> 16) & 0xFF, (colour2 >> 16) & 0xFF, (colour3 >> 16) & 0xFF, mu);
        int g = cubicInterpolateColour((colour0 >> 8) & 0xFF, (colour1 >> 8) & 0xFF, (colour2 >> 8) & 0xFF, (colour3 >> 8) & 0xFF, mu);
        int b = cubicInterpolateColour(colour0 & 0xFF, colour1 & 0xFF, colour2 & 0xFF, colour3 & 0xFF, mu);
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
