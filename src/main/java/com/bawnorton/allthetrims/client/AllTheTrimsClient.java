package com.bawnorton.allthetrims.client;

import com.bawnorton.allthetrims.client.adapters.AllTheTrimsTrimModelLoaderAdapter;
import com.bawnorton.allthetrims.client.adapters.ElytraTrimModelLoaderAdapter;
import com.bawnorton.allthetrims.client.adapters.ElytraTrimsTrimRenderLayerAdapter;
import com.bawnorton.allthetrims.client.compat.Compat;
import com.bawnorton.runtimetrims.client.RuntimeTrimsClient;
import com.bawnorton.runtimetrims.client.model.item.ItemTrimModelLoader;
import com.bawnorton.runtimetrims.client.model.item.adapter.TrimModelLoaderAdapter;
import net.minecraft.registry.Registries;

public final class AllTheTrimsClient {
    public static void init() {
        TrimModelLoaderAdapter adapter = new AllTheTrimsTrimModelLoaderAdapter();
        ItemTrimModelLoader itemModelLoader = RuntimeTrimsClient.getItemModelLoader();
        itemModelLoader.registerAdapter(adapter, Registries.ITEM.stream().filter(adapter::canTrim).toList());

        if(Compat.getElytraTrimsCompat().isPresent()) {
            itemModelLoader.registerAdapter(new ElytraTrimModelLoaderAdapter(), ElytraTrimModelLoaderAdapter.APPLICABLE);
            RuntimeTrimsClient.getShaderManager().registerAdapter(new ElytraTrimsTrimRenderLayerAdapter(), ElytraTrimsTrimRenderLayerAdapter.APPLICABLE);
        }
    }
}

