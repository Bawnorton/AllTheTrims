package com.bawnorton.allthetrims.client;

import com.bawnorton.allthetrims.client.api.DynamicTrimRenderer;
import com.bawnorton.allthetrims.client.config.Config;
import com.bawnorton.allthetrims.client.config.ConfigManager;
import com.bawnorton.allthetrims.client.model.DynamicTrimModelLoader;
import com.bawnorton.allthetrims.client.model.adapter.ElytraTrimModelLoaderAdapter;
import com.bawnorton.allthetrims.client.palette.TrimPalettes;
import com.bawnorton.allthetrims.client.render.DynamicTrimShaderManager;
import com.bawnorton.allthetrims.client.render.LayerData;

public final class AllTheTrimsClient {
    private static final LayerData layerData = new LayerData();
    private static final TrimPalettes trimPalettes = new TrimPalettes();
    private static final DynamicTrimRenderer trimRenderer = new DynamicTrimRenderer();
    private static final DynamicTrimShaderManager shaderManager = new DynamicTrimShaderManager();
    private static final DynamicTrimModelLoader modelLoader = new DynamicTrimModelLoader(layerData);
    private static final ConfigManager configManager = new ConfigManager();

    public static void init() {
        modelLoader.registerAdpater(new ElytraTrimModelLoaderAdapter(), ElytraTrimModelLoaderAdapter.APPLICABLE);
    }

    public static TrimPalettes getTrimPalettes() {
        return trimPalettes;
    }

    public static DynamicTrimRenderer getTrimRenderer() {
        return trimRenderer;
    }

    public static DynamicTrimShaderManager getShaderManager() {
        return shaderManager;
    }

    public static DynamicTrimModelLoader getModelLoader() {
        return modelLoader;
    }

    public static LayerData getLayerData() {
        return layerData;
    }

    public static Config getConfig() {
        return configManager.getOrLoadConfig();
    }

    public static void saveConfig() {
        configManager.saveConfig();
    }
}

