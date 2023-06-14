package com.bawnorton.allthetrims.util;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

// json representation of the armor trim atlas
@SuppressWarnings("unused")
public class ArmorTrimAtlas {
    public List<Source> sources;

    public static ArmorTrimAtlas fromJson(BufferedReader reader) {
        return new Gson().fromJson(reader, ArmorTrimAtlas.class);
    }

    public static InputStream toJson(ArmorTrimAtlas atlas) {
        return IOUtils.toInputStream(new Gson().toJson(atlas), "UTF-8");
    }

    public static class Source {
        public String type;
        public List<String> textures;
        public String palette_key;
        public Map<String, String> permutations;

        @Override
        public String toString() {
            return "Source{" +
                    "type='" + type + '\'' +
                    ", textures=" + textures +
                    ", palette_key='" + palette_key + '\'' +
                    ", permutations=" + permutations +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ArmorTrimAtlas{" +
                "sources=" + sources +
                '}';
    }
}
