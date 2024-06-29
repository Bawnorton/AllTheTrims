package com.bawnorton.allthetrims.client.compat.recipebrowser.emi;

import com.bawnorton.allthetrims.client.compat.recipebrowser.CyclingStack;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.gui.DrawContext;
import java.util.List;

public final class CyclingEmiStack extends CyclingStack<EmiStack> implements EmiRenderable {
    public CyclingEmiStack(List<EmiStack> entries, int duration) {
        super(entries, duration);
    }

    @Override
    public void render(DrawContext draw, int x, int y, float delta) {
        entries.get(index).render(draw, x, y, delta);
    }
}
