package com.bawnorton.allthetrims.versioned;

import net.minecraft.util.Identifier;

public final class VIdentifier {
    public static Identifier of(String namespace, String path) {
        return Identifier.of(namespace, path);
    }

    public static Identifier ofVanilla(String path) {
        return Identifier.ofVanilla(path);
    }
}
