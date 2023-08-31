package dev.pfaff.jacksoning.mixin;

import dev.pfaff.jacksoning.Constants;
import dev.pfaff.jacksoning.server.GameState;
import dev.pfaff.jacksoning.server.IGame;
import dev.pfaff.jacksoning.server.JacksoningServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer implements IGame {
	@Unique
	public final GameState state = new GameState();

	@Override
	public final GameState state() {
		return state;
	}

	@Inject(method = "initScoreboard", at = @At(value = "TAIL"))
	private final void init$jacksoning(PersistentStateManager persistentStateManager, CallbackInfo ci) {
		persistentStateManager.getOrCreate(nbt -> {
			this.state.inner.readNbt(nbt);
			return this.state.inner;
		}, () -> this.state.inner, Constants.PERSISTENT_STATE_ID);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private final void tick$jacksoning(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		state.tick((MinecraftServer) (Object) this);
	}
}
