package com.bawnorton.allthetrims.client;

import com.bawnorton.allthetrims.client.compat.Compat;
import com.bawnorton.allthetrims.client.config.Config;
import com.bawnorton.allthetrims.client.config.ConfigManager;
import com.bawnorton.allthetrims.client.model.TrimModelLoader;
import com.bawnorton.allthetrims.client.model.adapter.DefaultTrimModelLoaderAdapter;
import com.bawnorton.allthetrims.client.model.adapter.ElytraTrimModelLoaderAdapter;
import com.bawnorton.allthetrims.client.palette.TrimPalette;
import com.bawnorton.allthetrims.client.palette.TrimPalettes;
import com.bawnorton.allthetrims.client.render.LayerData;
import com.bawnorton.allthetrims.client.render.TrimRenderer;
import com.bawnorton.allthetrims.client.render.adapter.DefaultTrimRendererAdapter;
import com.bawnorton.allthetrims.client.render.adapter.ShowMeYourSkinTrimRendererAdapter;
import com.bawnorton.allthetrims.client.shader.TrimShaderManager;
import com.bawnorton.allthetrims.client.shader.adapter.DefaultTrimRenderLayerAdapter;
import com.bawnorton.allthetrims.client.shader.adapter.ElytraTrimsTrimRenderLayerAdapter;
import com.bawnorton.allthetrims.client.shader.adapter.ShowMeYourSkinTrimRenderLayerAdapter;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraft.item.trim.ArmorTrim;

import static com.bawnorton.allthetrims.AllTheTrims.MODEL_INDEX;

public final class AllTheTrimsClient {
    private static final LayerData layerData = new LayerData();
    private static final TrimPalettes trimPalettes = new TrimPalettes();
    private static final TrimRenderer trimRenderer = new TrimRenderer();
    private static final TrimModelLoader modelLoader = new TrimModelLoader(layerData);
    private static final TrimShaderManager shaderManager = new TrimShaderManager();
    private static final ConfigManager configManager = new ConfigManager();

    public static void init() {
        modelLoader.setDefaultAdapter(new DefaultTrimModelLoaderAdapter());

        if(Compat.getElytraTrimsCompat().isPresent()) {
            modelLoader.registerAdapter(new ElytraTrimModelLoaderAdapter(), ElytraTrimModelLoaderAdapter.APPLICABLE);
            shaderManager.registerAdapter(new ElytraTrimsTrimRenderLayerAdapter(), ElytraTrimsTrimRenderLayerAdapter.APPLICABLE);
        }

        if(Compat.getShowMeYourSkinCompat().isPresent()) {
            shaderManager.setDefaultAdapter(new ShowMeYourSkinTrimRenderLayerAdapter());
            trimRenderer.setDefaultAdapter(new ShowMeYourSkinTrimRendererAdapter());

        } else {
            shaderManager.setDefaultAdapter(new DefaultTrimRenderLayerAdapter());
            trimRenderer.setDefaultAdapter(new DefaultTrimRendererAdapter());
        }
    }

    public static boolean isDynamic(ArmorTrim trim) {
        return trim.getMaterial().value().itemModelIndex() == MODEL_INDEX || getConfig().overrideExisting;
    }

    public static TrimPalettes getTrimPalettes() {
        return trimPalettes;
    }

    public static TrimRenderer getTrimRenderer() {
        return trimRenderer;
    }

    public static TrimModelLoader getModelLoader() {
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

    public static TrimShaderManager getShaderManger() {
        return shaderManager;
    }

    public static RenderLayer getTrimRenderLayer(Item trimmed, ArmorTrim trim) {
        Item trimMaterial = trim.getMaterial().value().ingredient().value();
        TrimPalette palette = trimPalettes.getOrGeneratePalette(trimMaterial);
        return shaderManager.getTrimRenderLayer(trimmed, palette);
    }
}

