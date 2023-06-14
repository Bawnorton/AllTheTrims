package com.bawnorton.allthetrims;

import com.bawnorton.allthetrims.util.ImageUtil;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AllTheTrimsClient implements ClientModInitializer {
	public static final Map<Identifier, BufferedImage> paletteImages = new HashMap<>();

	@Override
	public void onInitializeClient() {
		AllTheTrims.LOGGER.info("Initializing AllTheTrims Client");
	}

	public static void addPaletteImage(Identifier id, NativeImage image) {
		String updatedPath = id.getPath();
		if(updatedPath.startsWith("block/")) updatedPath = updatedPath.substring(6);
		if(updatedPath.startsWith("item/")) updatedPath = updatedPath.substring(5);
		id = new Identifier(id.getNamespace(), updatedPath);
		paletteImages.put(id, ImageUtil.convertNativeToPaletteImg(image));
	}

	public static InputStream getPaletteImageInputStream(Identifier id) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write(paletteImages.getOrDefault(id, ImageUtil.getBlankPalette()), "png", baos);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return new ByteArrayInputStream(baos.toByteArray());
	}
}