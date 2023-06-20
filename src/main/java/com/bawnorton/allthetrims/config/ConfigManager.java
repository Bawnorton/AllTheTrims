package com.bawnorton.allthetrims.config;

import com.bawnorton.allthetrims.AllTheTrims;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path configPath = FabricLoader.getInstance().getConfigDir().resolve("allthetrims.json");

    public static void loadConfig() {
        Config config = load();

        if(config.ignoreWhitelist == null) config.ignoreWhitelist = true;
        if(config.debug == null) config.debug = false;

        Config.update(config);
        save();
        AllTheTrims.LOGGER.info("Config loaded");
    }

    private static Config load() {
        Config config = Config.getInstance();
        try {
            if (!Files.exists(configPath)) {
                Files.createDirectories(configPath.getParent());
                Files.createFile(configPath);
                return config;
            }
            try {
                config = GSON.fromJson(Files.newBufferedReader(configPath), Config.class);
            } catch (JsonSyntaxException e) {
                AllTheTrims.LOGGER.error("Failed to parse config file, using default config");
                config = new Config();
            }
        } catch (IOException e) {
            AllTheTrims.LOGGER.error("Failed to load config", e);
        }
        return config;
    }

    private static void save() {
        try {
            Files.write(configPath, GSON.toJson(Config.getInstance()).getBytes());
        } catch (IOException e) {
            AllTheTrims.LOGGER.error("Failed to save config", e);
        }
    }

    public static void saveConfig() {
        save();
        AllTheTrims.LOGGER.info("Saved client config");
    }
}
