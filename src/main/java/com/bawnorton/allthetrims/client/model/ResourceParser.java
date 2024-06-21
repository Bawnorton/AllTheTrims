package com.bawnorton.allthetrims.client.model;

import com.bawnorton.allthetrims.client.model.json.TextureLayers;
import com.bawnorton.allthetrims.client.model.json.serialisation.TextureLayersSerializer;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourcePack;
import org.apache.commons.io.IOUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class ResourceParser {
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(TextureLayers.class, new TextureLayersSerializer())
            .setPrettyPrinting()
            .create();

    public <T> T fromResource(Resource resource, Class<T> clazz) {
        try(BufferedReader reader = resource.getReader()) {
            return GSON.fromJson(reader, clazz);
        } catch (IOException e) {
            return null;
        }
    }

    public Resource toResource(ResourcePack resourcePack, Object object) {
        return new Resource(resourcePack, () -> IOUtils.toInputStream(GSON.toJson(object), StandardCharsets.UTF_8));
    }
}
