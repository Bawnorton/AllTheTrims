package com.bawnorton.allthetrims.client.compat.yacl;

import com.bawnorton.allthetrims.client.compat.Compat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import java.net.URI;

public final class YACLConfigScreenFactory {
    public static Screen createScreen(MinecraftClient client, Screen parent) {
        return Compat.getYaclImpl().map(impl -> impl.createConfigScreen(parent)).orElse(new ConfirmScreen((result) -> {
            if (result) {
                Util.getOperatingSystem().open(URI.create("https://modrinth.com/mod/yacl/versions"));
            }
            client.setScreen(parent);
        }, Text.translatable("allthetrims.yacl.not_installed"), Text.translatable("allthetrims.yacl.not_installed.message"), ScreenTexts.YES, ScreenTexts.NO));
    }
}
