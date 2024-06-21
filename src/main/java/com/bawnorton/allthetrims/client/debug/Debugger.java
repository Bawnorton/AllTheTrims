package com.bawnorton.allthetrims.client.debug;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import com.bawnorton.allthetrims.platform.Platform;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import javax.imageio.ImageIO;
import net.minecraft.resource.Resource;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public final class Debugger {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private static final Path ATT_DEBUG = Platform.getGameDir().resolve("att-debug");

    public static void createImage(String path, BufferedImage image) {
        if(!AllTheTrimsClient.getConfig().debug) return;
        try {
            File file = ATT_DEBUG.resolve(path).toFile();
            file.mkdirs();
            file.createNewFile();
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            AllTheTrims.LOGGER.error("Could not create debug file", e);
        }
    }

    public static void createJson(String path, Resource resource) {
        if(!AllTheTrimsClient.getConfig().debug) return;
        try {
            path = path.replace(":", File.separator);
            JsonReader reader = new JsonReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            JsonElement jsonElement = JsonParser.parseReader(reader);

            int lastSlash = path.lastIndexOf('/');
            String dir = path.substring(0, lastSlash);
            Path dirPath = ATT_DEBUG.resolve(dir);
            File dirFile = dirPath.toFile();
            dirFile.mkdirs();
            Path filePath = dirPath.resolve(path.substring(lastSlash + 1));
            File file = filePath.toFile();
            file.createNewFile();

            try (FileWriter fileWriter = new FileWriter(file)) {
                GSON.toJson(jsonElement, fileWriter);
            }
        } catch (IOException e) {
            AllTheTrims.LOGGER.error("Could not create debug file", e);
        }
    }
}
