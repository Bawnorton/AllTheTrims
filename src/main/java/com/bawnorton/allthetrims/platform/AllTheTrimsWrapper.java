package com.bawnorton.allthetrims.platform;

import com.bawnorton.allthetrims.AllTheTrims;

//? if fabric {
import net.fabricmc.api.ModInitializer;

public final class AllTheTrimsWrapper implements ModInitializer {
    @Override
    public void onInitialize() {
        AllTheTrims.init();
    }
}
//?} elif neoforge {
/*import net.neoforged.fml.common.Mod;

@Mod(AllTheTrims.MOD_ID)
public final class AllTheTrimsWrapper {
    public AllTheTrimsWrapper() {
        AllTheTrims.init();
    }
}
*///?}
