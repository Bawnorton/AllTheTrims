package com.bawnorton.allthetrims.mixin;

import com.bawnorton.allthetrims.AllTheTrims;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Lifecycle;
import net.minecraft.SharedConstants;
import net.minecraft.item.Item;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin(RegistryLoader.class)
public abstract class RegistryLoaderMixin {
    @SuppressWarnings("unchecked")
    @Inject(
            method = "loadFromResource(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/registry/RegistryOps$RegistryInfoGetter;Lnet/minecraft/registry/MutableRegistry;Lcom/mojang/serialization/Decoder;Ljava/util/Map;)V",
            at = @At("TAIL")
    )
    private static <E> void addAllTrimMaterialsToRegistry(ResourceManager resourceManager, RegistryOps.RegistryInfoGetter infoGetter, MutableRegistry<E> registry, Decoder<E> elementDecoder, Map<RegistryKey<?>, Exception> errors, CallbackInfo ci) {
        if (registry.getKey().equals(RegistryKeys.TRIM_MATERIAL)) {
            boolean valid = false;
            if(registry instanceof SimpleRegistry<?> simpleRegistry) {
                if (!simpleRegistry.isEmpty()) {
                    valid = simpleRegistry.iterator().next() instanceof ArmorTrimMaterial;
                }
            }
            if(!valid) {
                // Sanity check before unchecked cast. Should never happen
                AllTheTrims.LOGGER.error("Could not add materials to registry. AllTheTrims will not work, expected \"{} for {}\" but found \"{} for {}\".",
                        SimpleRegistry.class.getSimpleName(),
                        ArmorTrimMaterial.class.getSimpleName(),
                        registry.getClass().getSimpleName(),
                        registry.isEmpty() ? "<empty>" : registry.iterator().next().getClass()
                );
                return;
            }

            SimpleRegistry<ArmorTrimMaterial> trimMaterialRegistry = (SimpleRegistry<ArmorTrimMaterial>) registry;

            Set<Item> includedMaterials = trimMaterialRegistry.stream()
                    .map(trimMaterial -> trimMaterial.ingredient().value())
                    .collect(Collectors.toSet());

            Set<Pair<Identifier, RegistryEntry<Item>>> toInclude = Registries.ITEM.stream()
                    .filter(item -> !includedMaterials.contains(item))
                    .map(item -> new Pair<>(Registries.ITEM.getId(item), Registries.ITEM.getEntry(item)))
                    .collect(Collectors.toSet());

            RegistryEntryInfo info = new RegistryEntryInfo(
                    Optional.of(new VersionedIdentifier(
                            AllTheTrims.MOD_ID,
                            "runtime_trim_materials",
                            SharedConstants.VERSION_NAME
                    )),
                    Lifecycle.stable()
            );

            for (Pair<Identifier, RegistryEntry<Item>> itemRef : toInclude) {
                RegistryKey<ArmorTrimMaterial> trimRegKey = RegistryKey.of(trimMaterialRegistry.getKey(), itemRef.getLeft());
                ArmorTrimMaterial itemMaterial = new ArmorTrimMaterial(
                        AllTheTrims.DYNAMIC,
                        itemRef.getRight(),
                        AllTheTrims.MODEL_INDEX,
                        Map.of(),
                        Text.translatable("allthetrims.material", itemRef.getRight().value().getName().getString())
                );
                trimMaterialRegistry.add(trimRegKey, itemMaterial, info);
            }

            AllTheTrims.LOGGER.info("Added {} new trim materials!", toInclude.size());
        }
    }
}
