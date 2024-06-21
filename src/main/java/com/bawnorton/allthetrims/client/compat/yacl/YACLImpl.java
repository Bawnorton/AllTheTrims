package com.bawnorton.allthetrims.client.compat.yacl;

import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import com.bawnorton.allthetrims.client.compat.Compat;
import com.bawnorton.allthetrims.client.compat.elytratrims.ElytraTrimsCompat;
import com.bawnorton.allthetrims.client.config.Config;
import com.bawnorton.allthetrims.client.palette.TrimPalette;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import java.util.Locale;

public final class YACLImpl {
    public Screen getConfigScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("allthetrims.yacl.title"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("allthetrims.yacl.category.general"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("allthetrims.yacl.option.use_legacy_renderer"))
                                .description(OptionDescription.of(Text.translatable("allthetrims.yacl.description.use_legacy_renderer")))
                                .binding(false, () -> AllTheTrimsClient.getConfig().useLegacyRenderer, value -> AllTheTrimsClient.getConfig().useLegacyRenderer = value)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("allthetrims.yacl.option.debug"))
                                .description(OptionDescription.of(Text.translatable("allthetrims.yacl.description.debug")))
                                .binding(false, () -> AllTheTrimsClient.getConfig().debug, value -> AllTheTrimsClient.getConfig().debug = value)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Config.PaletteSorting>createBuilder()
                                .name(Text.translatable("allthetrims.yacl.option.palette_sorting"))
                                .description(OptionDescription.of(Text.translatable("allthetrims.yacl.description.palette_sorting")))
                                .binding(Config.PaletteSorting.COLOUR, () -> AllTheTrimsClient.getConfig().paletteSorting, value -> {
                                    AllTheTrimsClient.getConfig().paletteSorting = value;
                                    AllTheTrimsClient.getTrimPalettes().regenerate();
                                    AllTheTrimsClient.getShaderManager().clearRenderLayerCache();
                                    Compat.getElytraTrimsCompat().ifPresent(ElytraTrimsCompat::clearRenderLayerCache);
                                })
                                .controller(option -> EnumControllerBuilder.create(option)
                                        .enumClass(Config.PaletteSorting.class)
                                        .formatValue(value -> Text.translatable("allthetrims.yacl.option.palette_sorting.%s".formatted(value.name().toLowerCase(Locale.ENGLISH)))))
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("allthetrims.yacl.option.override_existing"))
                                .description(OptionDescription.of(Text.translatable("allthetrims.yacl.description.override_existing")))
                                .binding(false, () -> AllTheTrimsClient.getConfig().overrideExisting, value -> {
                                    AllTheTrimsClient.getConfig().overrideExisting = value;
                                    MinecraftClient.getInstance().reloadResources();
                                })
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("allthetrims.yacl.option.animate"))
                                .description(OptionDescription.of(Text.translatable("allthetrims.yacl.description.animate")))
                                .binding(false, () -> AllTheTrimsClient.getConfig().animate, value -> {
                                    AllTheTrimsClient.getConfig().animate = value;
                                    AllTheTrimsClient.getShaderManager().clearRenderLayerCache();
                                    Compat.getElytraTrimsCompat().ifPresent(ElytraTrimsCompat::clearRenderLayerCache);
                                    if(!value) {
                                        AllTheTrimsClient.getTrimPalettes().forEach(TrimPalette::recomputeColourArr);
                                    }
                                })
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Text.translatable("allthetrims.yacl.option.time_between_cycles"))
                                .description(OptionDescription.of(Text.translatable("allthetrims.yacl.description.time_between_cycles")))
                                .binding(75, () -> AllTheTrimsClient.getConfig().timeBetweenCycles, value -> AllTheTrimsClient.getConfig().timeBetweenCycles = value)
                                .controller(option -> IntegerSliderControllerBuilder.create(option)
                                        .range(0, 500)
                                        .step(5)
                                        .formatValue(value -> Text.of("%sms".formatted(value))))
                                .build())
                        .build())
                .save(AllTheTrimsClient::saveConfig)
                .build()
                .generateScreen(parent);
    }
}
