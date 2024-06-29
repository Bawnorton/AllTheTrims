package com.bawnorton.allthetrims.client.compat.recipebrowser.jei;

import com.bawnorton.allthetrims.client.compat.recipebrowser.CyclingStack;
import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.DrawContext;
import java.util.List;

public final class CyclingDrawable extends CyclingStack<IDrawable> implements IDrawable {
    public CyclingDrawable(List<IDrawable> entries, int duration) {
        super(entries, duration);
    }

    @Override
    public int getWidth() {
        return entries.get(index).getWidth();
    }

    @Override
    public int getHeight() {
        return entries.get(index).getHeight();
    }

    @Override
    public void draw(DrawContext drawContext, int xOffset, int yOffset) {
        entries.get(index).draw(drawContext, xOffset, yOffset);
    }
}
