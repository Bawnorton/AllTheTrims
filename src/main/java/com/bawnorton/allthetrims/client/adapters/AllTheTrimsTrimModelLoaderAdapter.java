package com.bawnorton.allthetrims.client.adapters;

import com.bawnorton.runtimetrims.client.model.item.adapter.DefaultTrimModelLoaderAdapter;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;

public final class AllTheTrimsTrimModelLoaderAdapter extends DefaultTrimModelLoaderAdapter {
    @Override
    public boolean canTrim(Item item) {
        if (item instanceof ElytraItem) return false;

        return super.canTrim(item);
    }
}
