package com.bawnorton.allthetrims.client;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.client.compat.Compat;
import com.bawnorton.allthetrims.client.config.Config;
import com.bawnorton.allthetrims.client.config.ConfigManager;
import com.bawnorton.allthetrims.client.model.armour.ArmourTrimModelLoader;
import com.bawnorton.allthetrims.client.model.item.ItemTrimModelLoader;
import com.bawnorton.allthetrims.client.model.item.adapter.DefaultTrimModelLoaderAdapter;
import com.bawnorton.allthetrims.client.model.item.adapter.ElytraTrimModelLoaderAdapter;
import com.bawnorton.allthetrims.client.palette.TrimPalettes;
import com.bawnorton.allthetrims.client.render.LayerData;
import com.bawnorton.allthetrims.client.render.TrimRenderer;
import com.bawnorton.allthetrims.client.render.adapter.DefaultTrimRendererAdapter;
import com.bawnorton.allthetrims.client.render.adapter.ShowMeYourSkinTrimRendererAdapter;
import com.bawnorton.allthetrims.client.shader.TrimShaderManager;
import com.bawnorton.allthetrims.client.shader.adapter.DefaultTrimRenderLayerAdapter;
import com.bawnorton.allthetrims.client.shader.adapter.ElytraTrimsTrimRenderLayerAdapter;
import com.bawnorton.allthetrims.client.shader.adapter.ShowMeYourSkinTrimRenderLayerAdapter;
import net.minecraft.item.trim.ArmorTrim;

public final class AllTheTrimsClient {
    private static final LayerData layerData = new LayerData();
    private static final TrimPalettes trimPalettes = new TrimPalettes();
    private static final TrimRenderer trimRenderer = new TrimRenderer();
    private static final ItemTrimModelLoader itemModelLoader = new ItemTrimModelLoader();
    private static final ArmourTrimModelLoader armourModelLoader = new ArmourTrimModelLoader(layerData);
    private static final TrimShaderManager shaderManager = new TrimShaderManager();
    private static final ConfigManager configManager = new ConfigManager();

    public static void init() {
        itemModelLoader.setDefaultAdapter(new DefaultTrimModelLoaderAdapter());

        if(Compat.getElytraTrimsCompat().isPresent()) {
            itemModelLoader.registerAdapter(new ElytraTrimModelLoaderAdapter(), ElytraTrimModelLoaderAdapter.APPLICABLE);
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
        return trim.getMaterial().value().itemModelIndex() == AllTheTrims.MODEL_INDEX || getConfig().overrideExisting;
    }

    public static TrimPalettes getTrimPalettes() {
        return trimPalettes;
    }

    public static TrimRenderer getTrimRenderer() {
        return trimRenderer;
    }

    /**
     * The inventory item models
     */
    public static ItemTrimModelLoader getItemModelLoader() {
        return itemModelLoader;
    }

    /**
     * The in-world armour models
     */
    public static ArmourTrimModelLoader getArmourModelLoader() {
        return armourModelLoader;
    }

    public static TrimShaderManager getShaderManger() {
        return shaderManager;
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

