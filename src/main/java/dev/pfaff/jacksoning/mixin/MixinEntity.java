package dev.pfaff.jacksoning.mixin;

import dev.pfaff.jacksoning.entities.IGameEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity {
	@Inject(method = "isInSameTeam", at = @At("HEAD"), cancellable = true)
	private void isInSameTeam$game(Entity other, CallbackInfoReturnable<Boolean> cir) {
		if (((Object) this) instanceof LivingEntity self && other instanceof IGameEntity ge) {
			var team = IGameEntity.cast(self).gameTeam();
			if (team != null && ge.gameTeam() == team) {
				cir.setReturnValue(true);
			}
		}
	}
}
