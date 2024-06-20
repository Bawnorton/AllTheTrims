package com.bawnorton.allthetrims.client.compat.modmenu;

import com.bawnorton.allthetrims.client.compat.Compat;
import com.bawnorton.allthetrims.client.compat.yacl.YACLImpl;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import java.net.URI;

public final class ModMenuImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return this::getConfigScreen;
    }

    private Screen getConfigScreen(Screen parent) {
        if(Compat.isYaclLoaded()) {
            return YACLImpl.getConfigScreen(parent);
        }

        return new ConfirmScreen((result) -> {
            if (result) {
                Util.getOperatingSystem().open(URI.create("https://modrinth.com/mod/yacl/versions"));
            }
            MinecraftClient.getInstance().setScreen(parent);
        }, Text.translatable("allthetrims.yacl.not_installed"), Text.translatable("allthetrims.yacl.not_installed.message"), ScreenTexts.YES, ScreenTexts.NO);
    }
}
