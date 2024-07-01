package com.bawnorton.allthetrims;

import com.bawnorton.allthetrims.platform.Platform;
import com.bawnorton.allthetrims.util.mixin.ConditionalMixin;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public final class AllTheTrimsCompatMixinPlugin implements IMixinConfigPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(AllTheTrimsCompatMixinPlugin.class);

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        try {
            List<AnnotationNode> annotationNodes = MixinService.getService()
                    .getBytecodeProvider()
                    .getClassNode(mixinClassName).visibleAnnotations;
            if (annotationNodes == null) return true;

            boolean shouldApply = true;
            for (AnnotationNode node : annotationNodes) {
                if (!node.desc.equals(Type.getDescriptor(ConditionalMixin.class))) continue;

                String modid = Annotations.getValue(node);
                if (Platform.isModLoaded(modid)) {
                    LOGGER.debug("%s is being applied because %s is loaded".formatted(mixinClassName, modid));
                    shouldApply = true;
                } else {
                    LOGGER.debug("%s is being applied because %s is not loaded".formatted(mixinClassName, modid));
                    shouldApply = false;
                }
            }
            return shouldApply;
        } catch (IOException | ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
