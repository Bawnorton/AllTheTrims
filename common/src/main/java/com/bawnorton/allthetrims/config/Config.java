package com.bawnorton.allthetrims.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Config {
    private static Config INSTANCE;

    public static Config getInstance() {
        if (INSTANCE == null) INSTANCE = new Config();
        return INSTANCE;
    }

    public static void update(Config config) {
        INSTANCE = config;
    }

    @Expose
    @SerializedName("ignore_whitelist")
    public Boolean ignoreWhitelist = true;

    @Expose
    @SerializedName("debug")
    public Boolean debug = false;


    @Override
    public String toString() {
        return "Config{" +
                "ignoreWhitelist=" + ignoreWhitelist +
                '}';
    }
}
