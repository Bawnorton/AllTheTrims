package com.bawnorton.allthetrims.client;

import com.bawnorton.allthetrims.client.adapters.ElytraTrimModelLoaderAdapter;
import com.bawnorton.allthetrims.client.adapters.ElytraTrimsTrimRenderLayerAdapter;
import com.bawnorton.allthetrims.client.compat.Compat;
import com.bawnorton.runtimetrims.client.RuntimeTrimsClient;
import com.bawnorton.runtimetrims.client.model.item.ItemTrimModelLoader;

public final class AllTheTrimsClient {
    public static void init() {
        ItemTrimModelLoader itemModelLoader = RuntimeTrimsClient.getItemModelLoader();

        if(Compat.getElytraTrimsCompat().isPresent()) {
            itemModelLoader.registerAdapter(new ElytraTrimModelLoaderAdapter(), ElytraTrimModelLoaderAdapter.APPLICABLE);
            RuntimeTrimsClient.getShaderManager().registerAdapter(new ElytraTrimsTrimRenderLayerAdapter(), ElytraTrimsTrimRenderLayerAdapter.APPLICABLE);
        }
    }
}

