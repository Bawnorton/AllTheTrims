package com.bawnorton.allthetrims.compat.client;

import com.bawnorton.allthetrims.config.Config;
import com.bawnorton.allthetrims.config.ConfigManager;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public abstract class YACLImpl {
    public static Screen getScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("yacl.allthetrims.title"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("yacl.allthetrims.category.general"))
                        .tooltip(Text.translatable("yacl.allthetrims.tooltip.general"))
                        .group(OptionGroup.createBuilder()
                                .name(Text.translatable("yacl.allthetrims.group.main"))
                                .description(OptionDescription.of(Text.translatable("yacl.allthetrims.description.main")))
                                .option(Option.createBuilder(boolean.class)
                                        .description(OptionDescription.createBuilder()
                                                .text((Text.translatable("yacl.allthetrims.description.ignorewhitelist")))
                                                .build())
                                        .name(Text.translatable("yacl.allthetrims.name.ignorewhitelist"))
                                        .binding(false, () -> Config.getInstance().ignoreWhitelist, (value) -> Config.getInstance().ignoreWhitelist = value)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build()
                                ).option(Option.createBuilder(boolean.class)
                                        .description(OptionDescription.createBuilder()
                                                .text(Text.translatable("yacl.allthetrims.description.debug1"),
                                                        Text.translatable("yacl.allthetrims.description.debug2"),
                                                        Text.translatable("yacl.allthetrims.description.debug3"))
                                                .build())
                                        .name(Text.translatable("yacl.allthetrims.name.debug"))
                                        .binding(false, () -> Config.getInstance().debug, (value) -> Config.getInstance().debug = value)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build()
                                ).build()
                        ).build()
                ).save(ConfigManager::saveConfig)
                .build()
                .generateScreen(parent);
    }
}
