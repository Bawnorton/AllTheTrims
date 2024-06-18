package com.bawnorton.allthetrims;

import com.bawnorton.allthetrims.client.palette.TrimPalettes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AllTheTrims {
    public static final String MOD_ID = "allthetrims";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final TrimPalettes TRIM_PALETTES = new TrimPalettes();

    public static void init() {
    }

    public static TrimPalettes getTrimPalettes() {
        return TRIM_PALETTES;
    }
}
