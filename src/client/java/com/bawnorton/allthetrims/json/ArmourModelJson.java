package com.bawnorton.allthetrims.json;

import java.util.List;
import java.util.Map;

public class ArmourModelJson implements JsonRepresentable {
    public String parent;
    public List<Override> overrides;
    public Map<String, String> textures;

    public static class Override {
        public String model;
        public Map<String, Float> predicate;

        public Override(String model, Map<String, Float> predicate) {
            this.model = model;
            this.predicate = predicate;
        }

        public String toString() {
            return "Override{" +
                    "model='" + model + '\'' +
                    ", predicate=" + predicate +
                    '}';
        }
    }

    public String toString() {
        return "ArmourModelJson{" +
                "parent='" + parent + '\'' +
                ", overrides=" + overrides +
                ", textures=" + textures +
                '}';
    }
}
