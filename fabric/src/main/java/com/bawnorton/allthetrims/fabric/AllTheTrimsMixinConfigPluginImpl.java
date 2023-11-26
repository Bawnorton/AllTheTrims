package com.bawnorton.allthetrims.fabric;

import com.bawnorton.allthetrims.AllTheTrimsMixinConfigPlugin;
import com.bawnorton.allthetrims.util.Comparison;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.impl.util.version.SemanticVersionImpl;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class AllTheTrimsMixinConfigPluginImpl implements IMixinConfigPlugin {
    public static boolean isModLoaded(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }

    public static boolean versionMatches(String modid, String versionPredicate) {
        if(versionPredicate.isEmpty()) return true;

        Optional<ModContainer> mod = FabricLoader.getInstance().getModContainer(modid);
        if (mod.isEmpty()) return false;

        Comparison comparison = Comparison.parseComparison(versionPredicate);
        String versionString = versionPredicate.substring(comparison.getSymbol().length());
        Version versionToCompare;
        try {
            versionToCompare = Version.parse(versionString);
            Version version = mod.get().getMetadata().getVersion();
            return comparison.satisfies(version.compareTo(versionToCompare));
        } catch (VersionParsingException e) {
            return false;
        }
    }

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return AllTheTrimsMixinConfigPlugin.testClass(mixinClassName);
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
