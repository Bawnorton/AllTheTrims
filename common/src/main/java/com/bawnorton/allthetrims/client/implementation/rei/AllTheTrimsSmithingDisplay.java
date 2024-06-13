package com.bawnorton.allthetrims.client.implementation.rei;

import com.bawnorton.allthetrims.mixin.accessor.SmithingTrimRecipeAccessor;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.displays.DefaultSmithingDisplay;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.item.trim.ArmorTrimMaterials;
import net.minecraft.item.trim.ArmorTrimPattern;
import net.minecraft.item.trim.ArmorTrimPatterns;
import net.minecraft.recipe.SmithingTransformRecipe;
import net.minecraft.recipe.SmithingTrimRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AllTheTrimsSmithingDisplay extends DefaultSmithingDisplay {
    public AllTheTrimsSmithingDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Optional<Identifier> location) {
        super(inputs, outputs, location);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ReiPluginImpl.TRIMMING;
    }

    public static List<AllTheTrimsSmithingDisplay> forRecipe(SmithingTrimRecipe recipe) {
        DynamicRegistryManager registryAccess = BasicDisplay.registryAccess();
        List<AllTheTrimsSmithingDisplay> displays = new ArrayList<>();
        SmithingTrimRecipeAccessor recipeAccessor = (SmithingTrimRecipeAccessor) recipe;
        for (ItemStack templateItem : recipeAccessor.getTemplate().getMatchingStacks()) {
            RegistryEntry.Reference<ArmorTrimPattern> trimPattern = ArmorTrimPatterns.get(registryAccess, templateItem)
                    .orElse(null);
            if (trimPattern == null) continue;

            for (ItemStack additionStack : recipeAccessor.getAddition().getMatchingStacks()) {
                RegistryEntry.Reference<ArmorTrimMaterial> trimMaterial = ArmorTrimMaterials.get(registryAccess, additionStack)
                        .orElse(null);
                if (trimMaterial == null) continue;

                ArmorTrim armorTrim = new ArmorTrim(trimMaterial, trimPattern);
                EntryIngredient.Builder baseItems = EntryIngredient.builder(), outputItems = EntryIngredient.builder();
                for (ItemStack item : recipeAccessor.getBase().getMatchingStacks()) {
                    Optional<ArmorTrim> trim = ArmorTrim.getTrim(registryAccess, item);
                    if (trim.isEmpty() || !trim.get().equals(trimPattern, trimMaterial)) {
                        ItemStack newItem = item.copy();
                        newItem.setCount(1);
                        if (ArmorTrim.apply(registryAccess, newItem, armorTrim)) {
                            baseItems.add(EntryStacks.of(item.copy()));
                            outputItems.add(EntryStacks.of(newItem));
                        }
                    }
                }
                displays.add(new AllTheTrimsSmithingDisplay(List.of(
                        EntryIngredients.of(templateItem),
                        baseItems.build(),
                        EntryIngredients.of(additionStack)
                ), List.of(outputItems.build()),
                        Optional.ofNullable(recipe.getId())));
            }
        }
        return displays;
    }
}
