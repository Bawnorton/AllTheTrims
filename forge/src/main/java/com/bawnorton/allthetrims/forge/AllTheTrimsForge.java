package com.bawnorton.allthetrims.forge;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.forge.client.AllTheTrimsForgeClient;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AllTheTrims.MOD_ID)
public class AllTheTrimsForge {
    public AllTheTrimsForge() {
        EventBuses.registerModEventBus(AllTheTrims.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        AllTheTrims.init();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(AllTheTrimsForgeClient::init);
    }
}
