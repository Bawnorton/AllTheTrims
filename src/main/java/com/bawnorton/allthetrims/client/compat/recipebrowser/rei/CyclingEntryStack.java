package com.bawnorton.allthetrims.client.compat.recipebrowser.rei;

import com.bawnorton.allthetrims.client.compat.recipebrowser.CyclingStack;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraft.client.gui.DrawContext;
import java.util.List;

public final class CyclingEntryStack<T> extends CyclingStack<EntryStack<T>> implements Renderer {
    public CyclingEntryStack(List<EntryStack<T>> entries, int duration) {
        super(entries, duration);
    }

    @Override
    public void render(DrawContext graphics, Rectangle bounds, int mouseX, int mouseY, float delta) {
        entries.get(index).render(graphics, bounds, mouseX, mouseY, delta);
    }
}
