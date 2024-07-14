package com.bawnorton.allthetrims.client.model.item.json;

import net.minecraft.util.Identifier;
import java.util.ArrayList;
import java.util.List;

public final class TrimmableItemModel {
    public String parent;
    public List<ModelOverride> overrides;
    public TextureLayers textures;

    private TrimmableItemModel(String parent, List<ModelOverride> overrides, TextureLayers textures) {
        this.parent = parent;
        this.overrides = overrides;
        this.textures = textures;
    }

    public void addOverride(ModelOverride override) {
        if (overrides == null) {
            overrides = new ArrayList<>();
        }

        overrides.add(override);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String parent;
        private List<ModelOverride> overrides = new ArrayList<>();
        private TextureLayers textures;

        public Builder parent(String parent) {
            this.parent = parent;
            return this;
        }

        public Builder parent(Identifier identifier) {
            return parent(identifier.toString());
        }

        public Builder override(ModelOverride override) {
            this.overrides.add(override);
            return this;
        }

        public Builder overrides(List<ModelOverride> overrides) {
            this.overrides = overrides;
            return this;
        }

        public Builder textures(TextureLayers textures) {
            this.textures = textures;
            return this;
        }

        public TrimmableItemModel build() {
            return new TrimmableItemModel(parent, overrides, textures);
        }
    }
}
