package com.bawnorton.allthetrims;

import com.bawnorton.mixinsquared.adjuster.tools.AdjustableAnnotationNode;
import com.bawnorton.mixinsquared.adjuster.tools.AdjustableInjectNode;
import com.bawnorton.mixinsquared.api.MixinAnnotationAdjuster;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.injection.Inject;
import java.util.List;

public final class AllTheTrimsAnnotationAdjuster implements MixinAnnotationAdjuster {
    @Override
    public AdjustableAnnotationNode adjust(List<String> targetClassNames, String mixinClassName, MethodNode handlerNode, AdjustableAnnotationNode annotationNode) {
        if(!mixinClassName.equals("net.irisshaders.iris.mixin.MixinProgram")) return annotationNode;
        if(!annotationNode.is(Inject.class)) return annotationNode;

        annotationNode.set("cancellable", annotationNode.<Boolean>get("cancellable").orElse(false));
        return annotationNode.as(AdjustableInjectNode.class).withCancellable(cancellable -> true); // actually log shader errors instead of failing.. thanks
    }
}
