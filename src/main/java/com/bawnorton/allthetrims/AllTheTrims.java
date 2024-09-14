package com.bawnorton.allthetrims;

import com.bawnorton.allthetrims.versioned.VIdentifier;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AllTheTrims {
    public static final String DYNAMIC = "dynamic"; // I've misspelled this too many times
    public static final String MOD_ID = "allthetrims";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final float MODEL_INDEX = 0.6632484f;

    public static Identifier id(String path) {
        return VIdentifier.of(MOD_ID, path);
    }
}
