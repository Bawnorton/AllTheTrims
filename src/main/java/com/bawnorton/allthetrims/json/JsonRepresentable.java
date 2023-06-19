package com.bawnorton.allthetrims.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.InputStream;

public interface JsonRepresentable {
    Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    static <T> T fromJson(BufferedReader reader, Class<T> clazz) {
        return GSON.fromJson(reader, clazz);
    }

    static String toJson(Object object) {
        return GSON.toJson(object);
    }
}
