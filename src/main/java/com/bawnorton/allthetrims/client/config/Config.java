package com.bawnorton.allthetrims.client.config;

public final class Config {
    public Boolean useLegacyRenderer;
    public Boolean debug;
    public PaletteSorting paletteSorting;
    public Boolean overrideExisting;
    public Boolean animate;
    public Integer timeBetweenCycles;

    public enum PaletteSorting {
        COLOUR, COLOUR_REVERSED,
        SATURATION, SATURATION_REVERSED,
        BRIGHTNESS, BRIGHTNESS_REVERSED;

        public boolean isReversed() {
            return this == COLOUR_REVERSED || this == SATURATION_REVERSED || this == BRIGHTNESS_REVERSED;
        }

        public boolean isColour() {
            return this == COLOUR || this == COLOUR_REVERSED;
        }

        public boolean isSaturation() {
            return this == SATURATION || this == SATURATION_REVERSED;
        }

        public boolean isBrightness() {
            return this == BRIGHTNESS || this == BRIGHTNESS_REVERSED;
        }
    }
}
