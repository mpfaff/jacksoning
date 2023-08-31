package dev.pfaff.jacksoning.mixin;

import net.minecraft.server.integrated.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(IntegratedServer.class)
public class MixinIntegratedServer {
	@Redirect(method = "setupServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;setOnlineMode(Z)V"))
	private final void setOnlineMode$cancel(IntegratedServer instance, boolean b) {
	}
}
