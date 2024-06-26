package com.bawnorton.allthetrims.client.shader;

import net.minecraft.client.render.RenderPhase;

public final class TrimPalettePhase extends RenderPhase {
    public TrimPalettePhase(String name, Runnable beginAction, Runnable endAction) {
        super(name, beginAction, endAction);
    }
}
