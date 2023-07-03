package com.bawnorton.allthetrims.fabric.client;

import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import net.fabricmc.api.ClientModInitializer;

public class AllTheTrimsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AllTheTrimsClient.init();
    }
}
