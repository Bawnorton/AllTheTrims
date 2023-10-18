package com.bawnorton.allthetrims.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Config {
    private static Config INSTANCE;
    @Expose
    @SerializedName("debug")
    public Boolean debug = false;

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
