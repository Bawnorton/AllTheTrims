package com.bawnorton.allthetrims.client.colour;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public final class OkLabHelper {
    public static @NotNull List<double[]> strechOkLab(int targetSize, int size, List<double[]> oklabPalette) {
        List<double[]> stretchedOKLab = new ArrayList<>(targetSize);
        for (int i = 0; i < targetSize; i++) {
            double t = (double) i / (targetSize - 1);
            int index1 = (int) Math.floor(t * (size - 1));
            int index2 = Math.min(index1 + 1, size - 1);
            double blend = (t * (size - 1)) - index1;

            double[] color1 = oklabPalette.get(index1);
            double[] color2 = oklabPalette.get(index2);
            double[] interpolatedColor = interpolateOKLab(color1, color2, blend);
            stretchedOKLab.add(interpolatedColor);
        }
        return stretchedOKLab;
    }

    public static double[] interpolateOKLab(double[] color1, double[] color2, double blend) {
        double[] result = new double[3];
        for (int i = 0; i < 3; i++) {
            result[i] = color1[i] * (1 - blend) + color2[i] * blend;
        }
        return result;
    }

    public static double[] average(double[][] okLabColours) {
        double[] result = new double[3];
        if (okLabColours.length == 0) return result;

        double sumL = 0;
        double sumA = 0;
        double sumB = 0;

        for (double[] okLabColour : okLabColours) {
            sumL += okLabColour[0];
            sumA += okLabColour[1];
            sumB += okLabColour[2];
        }

        result[0] = sumL / okLabColours.length;
        result[1] = sumA / okLabColours.length;
        result[2] = sumB / okLabColours.length;

        return result;
    }

    // https://bottosson.github.io/posts/oklab/
    // magic numbers galore
    public static double[] rgbToOKLab(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        double lR = gammaToLinear(r / 255D);
        double lG = gammaToLinear(g / 255D);
        double lB = gammaToLinear(b / 255D);

        double l = 0.4122214708 * lR + 0.5363325363 * lG + 0.0514459929 * lB;
        double m = 0.2119034982 * lR + 0.6806995451 * lG + 0.1073969566 * lB;
        double s = 0.0883024619 * lR + 0.2817188376 * lG + 0.6299787005 * lB;

        l = Math.cbrt(l);
        m = Math.cbrt(m);
        s = Math.cbrt(s);

        return new double[]{
                l * 0.2104542553 + m * 0.7936177850 + s * -0.0040720468,
                l * 1.9779984951 + m * -2.4285922050 + s * 0.4505937099,
                l * 0.0259040371 + m * 0.7827717662 + s * -0.8086757660
        };
    }

    public static int oklabToRGB(double[] oklab) {
        double L = oklab[0];
        double A = oklab[1];
        double B = oklab[2];

        double l = L + A * 0.3963377774 + B * 0.2158037573;
        double m = L + A * -0.1055613458 + B * -0.0638541728;
        double s = L + A * -0.0894841775 + B * -1.2914855480;

        l = l * l * l;
        m = m * m * m;
        s = s * s * s;

        double r = l * 4.0767416621 + m * -3.3077115913 + s * 0.2309699292;
        double g = l * -1.2684380046 + m * 2.6097574011 + s * -0.3413193965;
        double b = l * -0.0041960863 + m * -0.7034186147 + s * 1.7076147010;

        r = 255 * linearToGamma(r);
        g = 255 * linearToGamma(g);
        b = 255 * linearToGamma(b);

        int iR = (int) Math.clamp(r, 0, 255);
        int iG = (int) Math.clamp(g, 0, 255);
        int iB = (int) Math.clamp(b, 0, 255);

        return iR << 16 | iG << 8 | iB;
    }

    public static double gammaToLinear(double gamma) {
        if (gamma >= 0.04045) {
            return Math.pow((gamma + 0.055) / 1.055, 2.4);
        }
        return gamma / 12.92;
    }

    public static double linearToGamma(double linear) {
        if (linear >= 0.0031308) {
            return 1.055 * Math.pow(linear, 1 / 2.4) - 0.055;
        }
        return linear * 12.92;
    }
}
