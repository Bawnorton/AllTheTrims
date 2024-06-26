package com.bawnorton.allthetrims.platform;


//? if fabric {
import net.fabricmc.api.ModInitializer;

public final class AllTheTrimsWrapper implements ModInitializer {
    @Override
    public void onInitialize() {
    }
}
//?} elif neoforge {
/*import net.neoforged.fml.common.Mod;
import com.bawnorton.allthetrims.AllTheTrims;

@Mod(AllTheTrims.MOD_ID)
public final class AllTheTrimsWrapper {
    public AllTheTrimsWrapper() {
    }
}
*///?}
