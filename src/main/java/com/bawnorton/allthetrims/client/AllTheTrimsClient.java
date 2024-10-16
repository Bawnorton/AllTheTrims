package com.bawnorton.allthetrims.client;

import com.bawnorton.allthetrims.client.adapters.ElytraTrimModelLoaderAdapter;
import com.bawnorton.allthetrims.client.adapters.ElytraTrimsTrimRenderLayerAdapter;
import com.bawnorton.allthetrims.client.adapters.ElytraTrimsTrimRendererAdapter;
import com.bawnorton.allthetrims.client.compat.Compat;
import com.bawnorton.runtimetrims.client.RuntimeTrimsClient;

public final class AllTheTrimsClient {
    public static void init() {
        if(Compat.getElytraTrimsCompat().isPresent()) {
            RuntimeTrimsClient.getTrimRenderer().registerAdapter(new ElytraTrimsTrimRendererAdapter(), ElytraTrimsTrimRendererAdapter.APPLICABLE);
            RuntimeTrimsClient.getItemModelLoader().registerAdapter(new ElytraTrimModelLoaderAdapter(), ElytraTrimModelLoaderAdapter.APPLICABLE);
            RuntimeTrimsClient.getShaderManager().registerAdapter(new ElytraTrimsTrimRenderLayerAdapter(), ElytraTrimsTrimRenderLayerAdapter.APPLICABLE);
        }
    }
}

