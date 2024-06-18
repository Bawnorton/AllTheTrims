package com.bawnorton.allthetrims.client.render;

import net.minecraft.client.render.RenderPhase;

public final class TrimPalettePhase extends RenderPhase {
    public static final TrimPalettePhase NO_PALETTE = new TrimPalettePhase("trim_palette", () -> {}, () -> {});

    public TrimPalettePhase(String name, Runnable beginAction, Runnable endAction) {
        super(name, beginAction, endAction);
    }
}
