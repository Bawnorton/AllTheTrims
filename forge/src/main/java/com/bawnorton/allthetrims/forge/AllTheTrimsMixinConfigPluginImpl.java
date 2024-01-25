package com.bawnorton.allthetrims.forge;

import com.bawnorton.allthetrims.AllTheTrimsMixinConfigPlugin;
import com.bawnorton.allthetrims.util.Comparison;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class AllTheTrimsMixinConfigPluginImpl implements IMixinConfigPlugin {
    public static boolean isModLoaded(String modid) {
        List<ModInfo> mods = LoadingModList.get().getMods();
        for (ModInfo mod : mods) {
            if (mod.getModId().equals(modid)) {
                return true;
            }
        }
        return false;
    }

    public static boolean versionMatches(String modid, String versionPredicate) {
        if (versionPredicate.isEmpty()) return true;

        ModFileInfo mod = LoadingModList.get().getModFileById(modid);
        if (mod == null) return false;

        Comparison comparison = Comparison.parseComparison(versionPredicate);
        String versionString = versionPredicate.substring(comparison.getSymbol().length());
        DefaultArtifactVersion versionToCompare = new DefaultArtifactVersion(versionString);
        ArtifactVersion version = mod.getFile().getJarVersion();
        return comparison.satisfies(version.compareTo(versionToCompare));
    }

    @Override
    public void onLoad(String s) {

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
    public void acceptTargets(Set<String> set, Set<String> set1) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }
}
