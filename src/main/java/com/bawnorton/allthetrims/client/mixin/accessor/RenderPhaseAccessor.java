package com.bawnorton.allthetrims.client.mixin.accessor;

import net.minecraft.client.render.RenderPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderPhase.class)
public interface RenderPhaseAccessor {
    //? if neoforge {
    /*@Accessor("NO_TRANSPARENCY")
    static RenderPhase.Transparency getNoTransparency() {
        throw new AssertionError();
    }

    @Accessor("DISABLE_CULLING")
    static RenderPhase.Cull getDisableCulling() {
        throw new AssertionError();
    }

    @Accessor("ENABLE_LIGHTMAP")
    static RenderPhase.Lightmap getEnableLightmap() {
        throw new AssertionError();
    }

    @Accessor("ENABLE_OVERLAY_COLOR")
    static RenderPhase.Overlay getEnableOverlayColor() {
        throw new AssertionError();
    }

    @Accessor("VIEW_OFFSET_Z_LAYERING")
    static RenderPhase.Layering getViewOffsetZLayering() {
        throw new AssertionError();
    }

    @Accessor("LEQUAL_DEPTH_TEST")
    static RenderPhase.DepthTest getLequalDepthTest() {
        throw new AssertionError();
    }
    *///?}
}
