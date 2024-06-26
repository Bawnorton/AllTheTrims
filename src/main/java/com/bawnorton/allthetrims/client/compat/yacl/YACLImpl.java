package com.bawnorton.allthetrims.client.compat.yacl;

import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import com.bawnorton.allthetrims.client.config.Config;
import com.bawnorton.allthetrims.client.palette.TrimPalette;
import com.bawnorton.allthetrims.client.palette.TrimPalettes;
import com.bawnorton.allthetrims.client.shader.TrimShaderManager;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class YACLImpl {
    public Screen createConfigScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("allthetrims.yacl.title"))
                .category(createGeneralCategory())
                .save(AllTheTrimsClient::saveConfig)
                .build()
                .generateScreen(parent);
    }

    private ConfigCategory createGeneralCategory() {
        return ConfigCategory.createBuilder()
                .name(Text.translatable("allthetrims.yacl.category.general"))
                .option(createBooleanOption("use_legacy_renderer", false,
                        () -> getConfig().useLegacyRenderer,
                        value -> getConfig().useLegacyRenderer = value))
                .option(createBooleanOption("debug",
                        false,
                        () -> getConfig().debug,
                        value -> getConfig().debug = value))
                .option(createEnumOption("palette_sorting", Config.PaletteSorting.COLOUR,
                        Config.PaletteSorting.class,
                        () -> getConfig().paletteSorting,
                        value -> {
                            getConfig().paletteSorting = value;
                            getTrimPalettes().regenerate();
                            getShaderManager().clearRenderLayerCaches();
                        }))
                .option(createBooleanOption("override_existing", false,
                        () -> getConfig().overrideExisting,
                        value -> {
                            getConfig().overrideExisting = value;
                            MinecraftClient.getInstance().reloadResources();
                        }))
                .option(createBooleanOption("animate", false,
                        () -> getConfig().animate,
                        value -> {
                            getConfig().animate = value;
                            getShaderManager().clearRenderLayerCaches();
                            if (!value) {
                                getTrimPalettes().forEach(TrimPalette::computeColourArr);
                            }
                        }))
                .option(createIntegerOption("time_between_cycles", 75,
                        0, 500, 5,
                        () -> getConfig().timeBetweenCycles,
                        value -> getConfig().timeBetweenCycles = value,
                        value -> Text.of(value + "ms")))
                .option(createEnumOption("interpolation", Config.Interoplation.NONE,
                        Config.Interoplation.class,
                        () -> getConfig().animationInterpolation,
                        value -> {
                            getConfig().animationInterpolation = value;
                            getShaderManager().clearRenderLayerCaches();
                            getTrimPalettes().forEach(palette -> palette.computeInterpolation(value));
                        }))
                .build();
    }

    private <T extends Enum<T>> Option<T> createEnumOption(String key, T defaultValue, Class<T> enumClass, Supplier<T> getter, Consumer<T> setter) {
        return optionBuilder(key, defaultValue, getter, setter)
                .controller(option -> EnumControllerBuilder.create(option)
                        .enumClass(enumClass)
                        .formatValue(value -> option("%s.%s".formatted(key, value.name().toLowerCase(Locale.ENGLISH)))))
                .build();
    }

    private Option<Boolean> createBooleanOption(String key, boolean defaultValue, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        return optionBuilder(key, defaultValue, getter, setter)
                .controller(TickBoxControllerBuilder::create)
                .build();
    }

    private Option<Integer> createIntegerOption(String key, int defaultValue, int min, int max, int step, Supplier<Integer> getter, Consumer<Integer> setter, ValueFormatter<Integer> formatter) {
        return optionBuilder(key, defaultValue, getter, setter)
                .controller(option -> IntegerSliderControllerBuilder.create(option)
                        .range(min, max)
                        .step(step)
                        .formatValue(formatter))
                .build();
    }

    private <T> Option.Builder<T> optionBuilder(String key, T defaultValue, Supplier<T> getter, Consumer<T> setter) {
        return Option.<T>createBuilder()
                .name(option(key))
                .description(OptionDescription.createBuilder()
                        .text(description(key))
                        .build())
                .binding(defaultValue, getter, setter);
    }

    private Text option(String key) {
        return Text.translatable("allthetrims.yacl.option." + key);
    }

    private Text description(String key) {
        return Text.translatable("allthetrims.yacl.description." + key);
    }
    
    private Config getConfig() {
        return AllTheTrimsClient.getConfig();
    }
    
    private TrimShaderManager getShaderManager() {
        return AllTheTrimsClient.getShaderManger();
    }

    private TrimPalettes getTrimPalettes() {
        return AllTheTrimsClient.getTrimPalettes();
    }
}
