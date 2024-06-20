package com.bawnorton.allthetrims.client.config;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.platform.Platform;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .create();

    private static final Path configPath = Platform.getConfigDir().resolve("allthetrims.json");

    private Config config;

    public Config getOrLoadConfig() {
        if (config != null) return config;

        loadConfig();

        if(config.useLegacyRenderer == null) config.useLegacyRenderer = false;
        if(config.debug == null) config.debug = false;
        if(config.paletteSorting == null) config.paletteSorting = Config.PaletteSorting.BRIGHTNESS;
        if(config.overrideExisting == null) config.overrideExisting = false;
        if(config.animate == null) config.animate = false;
        if(config.timeBetweenCycles == null) config.timeBetweenCycles = 75;

        saveConfig();

        return config;
    }

    private void loadConfig() {
        try {
            if (!Files.exists(configPath)) {
                Files.createDirectories(configPath.getParent());
                Files.createFile(configPath);
            } else {
                try {
                    config = GSON.fromJson(Files.newBufferedReader(configPath), Config.class);
                } catch (JsonSyntaxException e) {
                    AllTheTrims.LOGGER.error("Failed to parse config file, using default config");
                }
            }
        } catch (IOException e) {
            AllTheTrims.LOGGER.error("Failed to load config", e);
        }
        if(config == null) {
            config = new Config();
        }
    }

    public void saveConfig() {
        try {
            Files.write(configPath, GSON.toJson(config).getBytes());
        } catch (IOException e) {
            AllTheTrims.LOGGER.error("Failed to save config", e);
        }
    }
}
