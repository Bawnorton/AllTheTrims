package com.bawnorton.allthetrims.config;

import com.bawnorton.allthetrims.AllTheTrims;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import dev.architectury.platform.Platform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class ConfigManager {
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .create();
    private static final Path configPath = Platform.getConfigFolder().resolve("allthetrims.json");

    public static void loadConfig() {
        Config config = load();

        if (config.debug == null) config.debug = false;
        if (config.trimRegistryMismatchMessage == null) config.trimRegistryMismatchMessage = "§b[All The Trims] §cTrim Registry Mismatch. §rPlease ensure that the client and server have the same mods with the same versions.";

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
