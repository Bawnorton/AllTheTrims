package com.bawnorton.allthetrims.fabric;

import com.bawnorton.allthetrims.AllTheTrims;
import net.fabricmc.api.ModInitializer;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.service.MixinService;

public class AllTheTrimsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        AllTheTrims.init();
    }
}
