package com.bawnorton.allthetrims.client.extend;

import net.minecraft.client.gl.GlUniform;

public interface ShaderProgramExtender {
    GlUniform allthetrims$getTrimPalette();
    GlUniform allthetrims$getDebug();
}
