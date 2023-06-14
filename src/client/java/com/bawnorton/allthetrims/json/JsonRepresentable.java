package com.bawnorton.allthetrims.json;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.InputStream;

public interface JsonRepresentable {
    static <T extends JsonRepresentable> T fromJson(BufferedReader reader, Class<T> clazz) {
        return new Gson().fromJson(reader, clazz);
    }

    default String toJson() {
        return new Gson().toJson(this);
    }

    default InputStream toInputStream() {
        return IOUtils.toInputStream(toJson(), "UTF-8");
    }
}
