package dev.pfaff.jacksoning.mixin;

import dev.pfaff.jacksoning.server.IGame;
import dev.pfaff.jacksoning.server.PlayerState;
import dev.pfaff.jacksoning.server.IGamePlayer;
import dev.pfaff.jacksoning.server.ServerSidebar;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity implements IGamePlayer {
	@Shadow
	@Final
	public MinecraftServer server;

	@Shadow
	@Final
	private static Logger LOGGER;

	@Unique
	public PlayerState state = new PlayerState.None();

	@Unique
	public ServerSidebar sidebar = new ServerSidebar();

	@Override
	public final PlayerState state() {
		return state;
	}

	@Override
	public final void setState(PlayerState state) {
		sidebar.roleChangeNotifier.update(state.role());
		this.state = state;
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private final void tick(CallbackInfo ci) {
		var p = (ServerPlayerEntity) (Object) this;
		if (IGame.cast(server).state().isRunning()) {
			state.tick(p);
		}
		sidebar.tick(p);
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
	private final void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
		state = PlayerState.readFromPlayerNbt(nbt);
	}

	@Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
	private final void writeCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
		state.writeToPlayerNbt(nbt);
	}
}
