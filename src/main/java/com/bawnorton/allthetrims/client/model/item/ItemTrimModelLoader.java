package com.bawnorton.allthetrims.client.model.item;

import com.bawnorton.allthetrims.AllTheTrims;
import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import com.bawnorton.allthetrims.client.debug.Debugger;
import com.bawnorton.allthetrims.client.render.LayerData;
import it.unimi.dsi.fastutil.Pair;
import javax.imageio.ImageIO;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Unique;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;

public final class ItemTrimModelLoader {
    private final LayerData layerData;
    private final Map<String, List<Integer>> trimTemplateColours = Collections.synchronizedMap(new HashMap<>());
    private final List<Identifier> skippedLayers = new ArrayList<>();

    public ItemTrimModelLoader(LayerData layerData) {
        this.layerData = layerData;
    }
    
    public Optional<Resource> loadLayeredResource(Identifier identifier, BufferedImage bufferedImage, Identifier originalIdentifier, int layer, ResourcePack resourcePack) {
        int layerColour = getLayerColour(bufferedImage, originalIdentifier.getPath(), layer);
        Pair<BufferedImage, Boolean> newImage;
        if(originalIdentifier.getPath().startsWith("textures/trims/items")) {
            newImage = maskToColour(bufferedImage, layerColour, layerColour);
        } else {
            newImage = maskToColour(bufferedImage, layerColour, 0xFFFFFFFF);
        }

        if(newImage.right()) {
            skippedLayers.add(identifier);
        } else {
            layerData.setMaxSupportedLayer(originalIdentifier, layer);
            Debugger.createImage("%s".formatted(originalIdentifier.getPath()), bufferedImage);
            Debugger.createImage("%s".formatted(identifier.getPath()), newImage.left());
        }

        return Optional.of(new Resource(resourcePack, () -> asInputStream(newImage.left())));
    }

    public Set<Map.Entry<String, Supplier<IntUnaryOperator>>> cleanPermutations(Set<Map.Entry<String, Supplier<IntUnaryOperator>>> permutations, Identifier textureId) {
        if(skippedLayers.contains(textureId)) return Collections.emptySet();

        String path = textureId.getPath();
        String pattern = ".*_\\d.png";
        Set<Map.Entry<String, Supplier<IntUnaryOperator>>> newPermutations = new HashSet<>();
        if (!path.matches(pattern)) {
            newPermutations.addAll(permutations);
        } else {
            for (Map.Entry<String, Supplier<IntUnaryOperator>> entry : permutations) {
                if (entry.getKey().equals(AllTheTrims.DYNAMIC)) {
                    newPermutations.add(entry);
                }
            }
        }
        if(AllTheTrimsClient.getConfig().overrideExisting) {
            newPermutations.removeIf(entry -> !entry.getKey().equals(AllTheTrims.DYNAMIC));
        }
        return newPermutations;
    }

    @Unique
    private int getLayerColour(BufferedImage bufferedImage, String trimId, int layer) {
        List<Integer> layerColours = trimTemplateColours.computeIfAbsent(trimId, id -> {
            int[] colours = bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null, 0, bufferedImage.getWidth());
            Set<Integer> uniqueColours = new HashSet<>();
            for (int colour : colours) {
                uniqueColours.add(colour);
            }
            uniqueColours.remove(0);
            return uniqueColours.stream()
                    .sorted(Comparator.comparingInt(i -> i))
                    .toList();
        });
        if (layer >= layerColours.size()) return -1;
        return layerColours.get(layer);
    }

    @Unique
    private Pair<BufferedImage, Boolean> maskToColour(BufferedImage bufferedImage, int mask, int toColour) {
        BufferedImage maskedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        if(mask == -1) return Pair.of(maskedImage, true);

        boolean empty = true;
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                int colour = bufferedImage.getRGB(x, y);
                int alpha = colour >> 24 & 255;
                if (alpha == 0) continue;

                if (colour == mask) {
                    empty = false;
                    maskedImage.setRGB(x, y, toColour);
                }
            }
        }
        return Pair.of(maskedImage, empty);
    }

    @Unique
    private InputStream asInputStream(BufferedImage bufferedImage) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", baos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }
}
