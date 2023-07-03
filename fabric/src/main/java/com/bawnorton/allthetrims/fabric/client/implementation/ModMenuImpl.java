package com.bawnorton.allthetrims.fabric.client.implementation;

import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return AllTheTrimsClient::getConfigScreen;
    }
}
