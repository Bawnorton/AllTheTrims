package com.bawnorton.allthetrims.json;

import java.util.List;
import java.util.Map;

public class ArmorTrimAtlas implements JsonRepresentable {
    public List<Source> sources;

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
