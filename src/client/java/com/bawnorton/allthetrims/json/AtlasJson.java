package com.bawnorton.allthetrims.json;

import java.util.List;
import java.util.Map;

public class AtlasJson implements JsonRepresentable {
    public List<Source> sources;

    public static class Source {
        public String type;
        public String source;
        public String prefix;
        public String resource;
        public List<String> textures;
        public String palette_key;
        public Map<String, String> permutations;

        @Override
        public String toString() {
            return "Source{" +
                    "type='" + type + '\'' +
                    ", source='" + source + '\'' +
                    ", prefix='" + prefix + '\'' +
                    ", resource='" + resource + '\'' +
                    ", textures=" + textures +
                    ", palette_key='" + palette_key + '\'' +
                    ", permutations=" + permutations +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "AtlasJson{" +
                "sources=" + sources +
                '}';
    }
}
