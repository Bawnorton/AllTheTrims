package com.bawnorton.allthetrims.compat;

import com.bawnorton.allthetrims.AllTheTrimsClient;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return AllTheTrimsClient::getConfigScreen;
    }
}
