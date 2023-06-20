package com.bawnorton.allthetrims.data;

import com.bawnorton.allthetrims.AllTheTrims;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class AllTheTrimsTags {
    public static final TagKey<Item> WHITELIST = TagKey.of(RegistryKeys.ITEM, new Identifier(AllTheTrims.MOD_ID, "whitelist"));
}
