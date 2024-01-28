package com.bawnorton.allthetrims.fabric.mixin.client.fapi;

import com.bawnorton.allthetrims.config.Config;
import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.sugar.Local;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = ClientConnection.class, priority = 1500)
public abstract class ClientConnectionMixinSquared {
    @SuppressWarnings({"MixinAnnotationTarget", "InvalidMemberReference", "UnresolvedMixinReference"})
    @TargetHandler(
            mixin = "net.fabricmc.fabric.mixin.networking.ClientConnectionMixin",
            name = "resendOnExceptionCaught"
    )
    @ModifyArg(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/text/MutableText;"))
    private Object[] replaceRegistryMismatchMessage(Object[] args, @Local(argsOnly = true) Throwable ex) {
        if (ex instanceof DecoderException) {
            String message = (String) args[0];
            if (message.contains("minecraft:trim_material")) {
                args[0] = Config.getInstance().trimRegistryMismatchMessage;
            }
        }
        return args;
    }
}
