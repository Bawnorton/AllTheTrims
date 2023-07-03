package com.bawnorton.allthetrims.fabric;

import com.bawnorton.allthetrims.AllTheTrims;
import net.fabricmc.api.ModInitializer;

public class AllTheTrimsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        AllTheTrims.init();
    }
}
