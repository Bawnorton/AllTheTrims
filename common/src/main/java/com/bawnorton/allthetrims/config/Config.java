package com.bawnorton.allthetrims.config;

public class Config {
    private static Config INSTANCE;

    public Boolean debug = false;

    public String trimRegistryMismatchMessage = "§b[All The Trims] §cTrim Registry Mismatch. §rPlease ensure that the client and server have the same mods with the same versions.";

    public static Config getInstance() {
        if (INSTANCE == null) INSTANCE = new Config();
        return INSTANCE;
    }

    public static void update(Config config) {
        INSTANCE = config;
    }

    @Override
    public String toString() {
        return "Config{" + "debug=" + debug + '}';
    }
}
