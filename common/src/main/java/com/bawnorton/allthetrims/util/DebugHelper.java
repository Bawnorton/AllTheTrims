package com.bawnorton.allthetrims.util;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.config.Config;
import dev.architectury.platform.Platform;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;

public abstract class DebugHelper {
    static {
        try {
            Path gameDir = Platform.getGameFolder();
            File debugDir = gameDir.resolve("att-debug").toFile();
            if(debugDir.exists()) {
                debugDir.delete();
            }
            debugDir.mkdirs();
        } catch (Exception e) {
            AllTheTrims.LOGGER.error("Failed to create debug directory", e);
        }
    }

    public static void createDebugFile(String directory, String filename, String content) {
        if(!Config.getInstance().debug) return;
        try {
            Path gameDir = Platform.getGameFolder();
            File debugDir = gameDir.resolve("att-debug").resolve(directory).toFile();
            debugDir.mkdirs();
            File debugFile = debugDir.toPath().resolve(filename.replace("/", "_")).toFile();
            debugFile.createNewFile();

            Writer writer = new FileWriter(debugFile);
            IOUtils.copy(IOUtils.toInputStream(content, "UTF-8"), writer, "UTF-8");
            writer.close();
        } catch (IOException e) {
            AllTheTrims.LOGGER.error("Failed to create debug file: " + filename, e);
        }
    }

    public static void saveLayeredTexture(BufferedImage image, String path) {
        if(!Config.getInstance().debug) return;
        try {
            Path gameDir = Platform.getGameFolder();
            File debugDir = gameDir.resolve("att-debug").resolve("textures").toFile();
            debugDir.mkdirs();
            File debugFile = debugDir.toPath().resolve(path.replace("/", "_")).toFile();
            debugFile.createNewFile();

            ImageIO.write(image, "png", debugFile);
        } catch (IOException e) {
            AllTheTrims.LOGGER.error("Failed to create debug image: " + path, e);
        }
    }

    public static void savePalette(BufferedImage image, String path) {
        if(!Config.getInstance().debug) return;
        try {
            Path gameDir = Platform.getGameFolder();
            File debugDir = gameDir.resolve("att-debug").resolve("palettes").toFile();
            debugDir.mkdirs();
            File debugFile = debugDir.toPath().resolve(path.replace("/", "_")).toFile();
            debugFile.createNewFile();

            ImageIO.write(image, "png", debugFile);
        } catch (IOException e) {
            AllTheTrims.LOGGER.error("Failed to create debug image: " + path, e);
        }
    }
}
