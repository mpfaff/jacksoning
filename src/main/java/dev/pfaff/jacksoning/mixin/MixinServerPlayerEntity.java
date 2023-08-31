package dev.pfaff.jacksoning.mixin;

import dev.pfaff.jacksoning.server.IGamePlayer;
import dev.pfaff.jacksoning.server.PlayerData;
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

import static dev.pfaff.jacksoning.server.PlayerData.PLAYER_NBT_KEY;
import static dev.pfaff.jacksoning.util.nbt.Codec.NBT_COMPOUND;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity implements IGamePlayer {
	@Shadow
	@Final
	public MinecraftServer server;

	@Shadow
	@Final
	private static Logger LOGGER;

	@Unique
	public PlayerData playerData = new PlayerData();

	@Unique
	public ServerSidebar sidebar = new ServerSidebar();

	@Override
	public final PlayerData data() {
		return playerData;
	}

	@Override
	public void data(PlayerData data) {
		playerData = data;
		// trigger change notifiers
		roleState(data.roleState);
	}

	@Override
	public ServerSidebar sidebar() {
		return sidebar;
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private final void tick(CallbackInfo ci) {
		var p = (ServerPlayerEntity) (Object) this;
		tickJacksoning();
		sidebar.tick(p);
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
	private final void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
		if (NBT_COMPOUND.getOrNull(nbt, PLAYER_NBT_KEY) instanceof NbtCompound inner) {
			playerData.readNbt(inner);
		}
	}

	@Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
	private final void writeCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
		NBT_COMPOUND.put(nbt, PLAYER_NBT_KEY, data().writeNbt());
	}
}
