package com.bawnorton.allthetrims.util;

import com.bawnorton.allthetrims.AllTheTrims;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;

public abstract class DebugHelper {
    public static void createDebugFile(String directory, String filename, String content) {
//        if(!FabricLoader.getInstance().isDevelopmentEnvironment()) return;
        try {
            Path gameDir = FabricLoader.getInstance().getGameDir();
            File debugDir = gameDir.resolve("att-debug").resolve(directory).toFile();
            debugDir.mkdirs();
            File debugFile = debugDir.toPath().resolve(filename).toFile();
            debugFile.createNewFile();

            Writer writer = new FileWriter(debugFile);
            IOUtils.copy(IOUtils.toInputStream(content, "UTF-8"), writer, "UTF-8");
            writer.close();
        } catch (IOException e) {
            AllTheTrims.LOGGER.error("Failed to create debug file: " + filename, e);
        }
    }
}
