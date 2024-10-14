package com.bawnorton.allthetrims.client.platform;

import com.bawnorton.allthetrims.client.AllTheTrimsClient;

/*? if fabric {*/
import net.fabricmc.api.ClientModInitializer;

public final class AllTheTrimsClientWrapper implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AllTheTrimsClient.init();
    }
}

/*?} elif neoforge {*/
/*import com.bawnorton.allthetrims.AllTheTrims;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = AllTheTrims.MOD_ID, dist = Dist.CLIENT)
public final class AllTheTrimsClientWrapper {
    public AllTheTrimsClientWrapper() {
        AllTheTrimsClient.init();
    }
}
*//*?}*/