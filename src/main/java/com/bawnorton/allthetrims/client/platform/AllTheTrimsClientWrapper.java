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
import com.bawnorton.allthetrims.client.compat.yacl.YACLConfigScreenFactory;
import net.minecraft.client.MinecraftClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = AllTheTrims.MOD_ID, dist = Dist.CLIENT)
public final class AllTheTrimsClientWrapper {
    public AllTheTrimsClientWrapper() {
        AllTheTrimsClient.init();
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (container, screen) -> YACLConfigScreenFactory.createScreen(MinecraftClient.getInstance(), screen));
    }
}
*//*?}*/