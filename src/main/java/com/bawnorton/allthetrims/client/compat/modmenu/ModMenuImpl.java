package com.bawnorton.allthetrims.client.compat.modmenu;

//? if fabric {
/*import com.bawnorton.allthetrims.client.compat.yacl.YACLConfigScreenFactory;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.MinecraftClient;

public final class ModMenuImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> YACLConfigScreenFactory.createScreen(MinecraftClient.getInstance(), parent);
    }
}
*///?}
