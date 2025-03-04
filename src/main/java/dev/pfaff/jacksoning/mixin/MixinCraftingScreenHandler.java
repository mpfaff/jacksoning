package dev.pfaff.jacksoning.mixin;

import dev.pfaff.jacksoning.player.IGamePlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.CraftingScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CraftingScreenHandler.class)
public final class MixinCraftingScreenHandler {
	@Inject(method = "canUse", at = @At(value = "HEAD"), cancellable = true)
	private final void canUse$notJackson(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
		// TODO: sync properties like role over the client and **also** do the check there to avoid the UI quickly
		//  closing
		if (player instanceof IGamePlayer p) {
			switch (p.gamePlayer().roleState().role()) {
				case Jackson, Mistress -> cir.setReturnValue(false);
				default -> {}
			}
		}
	}
}
