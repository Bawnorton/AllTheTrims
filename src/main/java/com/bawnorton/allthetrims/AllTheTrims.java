package com.bawnorton.allthetrims;

import com.bawnorton.allthetrims.adapters.AllTheTrimsMaterialRegistryAdapater;
import com.bawnorton.allthetrims.adapters.AllTheTrimsTagInjector;
import com.bawnorton.runtimetrims.RuntimeTrims;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AllTheTrims {
    public static final String MOD_ID = "allthetrims";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public static void init() {
        LOGGER.debug("Initializing AllTheTrims");
        RuntimeTrims.getTrimMaterialRegistryInjector().registerAdapter(new AllTheTrimsMaterialRegistryAdapater());
        RuntimeTrims.getTrimTagInjector().registerAdapter(new AllTheTrimsTagInjector());
    }
}
