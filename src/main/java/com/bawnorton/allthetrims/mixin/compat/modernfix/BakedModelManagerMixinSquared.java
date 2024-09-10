package com.bawnorton.allthetrims.mixin.compat.modernfix;

import com.bawnorton.allthetrims.client.AllTheTrimsClient;
import com.bawnorton.allthetrims.util.mixin.ConditionalMixin;
import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Modernfix's dynamic resources option greatly improves performance, however, ATT relies on being able to inject
 * models at runtime based on the models that are being loaded. This allows ATT to create the dynamic trim override
 * for any given piece of equipment and add the predicate to the loaded model. Now since Modernfix redirects the model
 * loading to instead rely on loading the models lazily, it skips over ATT's model injection, and thus, I need to inject
 * into their model loader to add the dynamic trim predicate override to any equipment models (and also provide the
 * model for the dynamic trim)
 * Ideally Modernfix would provide an API for this, but they don't, and stated that they wouldn't so I have to resort to
 * mixin'ing into their own mixin
 */
@SuppressWarnings("UnusedMixin")
@Mixin(value = BakedModelManager.class, priority = 1500)
@ConditionalMixin("modernfix")
public abstract class BakedModelManagerMixinSquared {
    @Unique
    private final Map<Identifier, Resource> allthetrims$dynamicTrimModels = new HashMap<>();

    @SuppressWarnings("UnresolvedMixinReference")
    @TargetHandler(
            mixin = "org.embeddedt.modernfix.common.mixin.perf.dynamic_resources.ModelManagerMixin",
            name = "loadSingleBlockModel"
    )
    @WrapOperation(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/resource/ResourceManager.getResource (Lnet/minecraft/util/Identifier;)Ljava/util/Optional;"
            ),
            require = 0
    )
    private Optional<Resource> addDynamicTrimModel(ResourceManager instance, Identifier id, Operation<Optional<Resource>> original) {
        if(allthetrims$dynamicTrimModels.containsKey(id)) return Optional.of(allthetrims$dynamicTrimModels.get(id));

        Optional<Resource> result = original.call(instance, id);
        AtomicReference<Resource> modified = new AtomicReference<>(result.orElse(null));
        result.ifPresent(resource -> AllTheTrimsClient.getItemModelLoader().loadModels(id, resource, (newId, newResource) -> {
            if(newId.equals(id)) {
                modified.set(newResource);
            } else {
                allthetrims$dynamicTrimModels.put(newId, newResource);
            }
        }));
        return Optional.ofNullable(modified.get());
    }
}
