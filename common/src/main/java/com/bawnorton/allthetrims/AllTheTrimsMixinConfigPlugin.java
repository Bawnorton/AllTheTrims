package com.bawnorton.allthetrims;

import com.bawnorton.allthetrims.annotation.ConditionalMixin;
import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import dev.architectury.injectables.annotations.ExpectPlatform;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class AllTheTrimsMixinConfigPlugin implements IMixinConfigPlugin {
    public static boolean testClass(String className) {
        try {
            List<AnnotationNode> annotationNodes = MixinService.getService()
                                                               .getBytecodeProvider()
                                                               .getClassNode(className).visibleAnnotations;
            if (annotationNodes == null) return true;

            for (AnnotationNode node : annotationNodes) {
                if (node.desc.equals(Type.getDescriptor(ConditionalMixin.class))) {
                    String modid = Annotations.getValue(node, "modid");
                    String versionPredicate = Annotations.getValue(node, "version", "");
                    boolean applyIfPresent = Annotations.getValue(node, "applyIfPresent", Boolean.TRUE);
                    if (isModLoaded(modid)) {
                        if (versionMatches(modid, versionPredicate)) {
                            AllTheTrims.LOGGER.debug("AllTheTrimsMixinPlugin: " + className + " is" + (applyIfPresent ? " " : " not ") + "being applied because " + modid + " is loaded" + (versionPredicate.isEmpty() ? "" : " and version predicate " + versionPredicate + " is satisfied"));
                            return applyIfPresent;
                        } else {
                            AllTheTrims.LOGGER.debug("AllTheTrimsMixinPlugin: " + className + " is" + (!applyIfPresent ? " " : " not ") + "being applied because " + modid + " is loaded but version predicate " + versionPredicate + " is not satisfied");
                            return !applyIfPresent;
                        }
                    } else {
                        AllTheTrims.LOGGER.debug("AllTheTrimsMixinPlugin: " + className + " is" + (!applyIfPresent ? " " : " not ") + "being applied because " + modid + " is not loaded");
                        return !applyIfPresent;
                    }
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @ExpectPlatform
    private static boolean isModLoaded(String modid) {
        throw new AssertionError();
    }

    @ExpectPlatform
    private static boolean versionMatches(String modid, String versionPredicate) {
        throw new AssertionError();
    }

    @Override
    public void onLoad(String mixinPackage) {
        MixinExtrasBootstrap.init();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetName, String className) {
        return testClass(className);
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
