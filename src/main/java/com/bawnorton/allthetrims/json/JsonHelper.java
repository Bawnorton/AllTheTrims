package com.bawnorton.allthetrims.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;

public class JsonHelper {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static <T> T fromJson(BufferedReader reader, Class<T> clazz) {
        return GSON.fromJson(reader, clazz);
    }

    public static <T> T fromJson(String string, Class<T> jsonObjectClass) {
        return GSON.fromJson(string, jsonObjectClass);
    }

    public static String toJson(Object object) {
        return GSON.toJson(object);
    }

}
