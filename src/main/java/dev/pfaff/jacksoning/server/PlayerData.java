package dev.pfaff.jacksoning.server;

import dev.pfaff.jacksoning.PlayerRole;
import dev.pfaff.jacksoning.util.codec.CodecException;
import dev.pfaff.jacksoning.util.nbt.Container;
import dev.pfaff.jacksoning.util.nbt.ContainerCodecHelper;
import dev.pfaff.jacksoning.util.nbt.NbtCompound;

import java.lang.invoke.MethodHandles;
import java.util.List;

import static dev.pfaff.jacksoning.util.nbt.ContainerCodecHelper.containerField;
import static dev.pfaff.jacksoning.util.nbt.NbtCodecs.NBT_INT;

public final class PlayerData implements Container {
	public static final String PLAYER_NBT_KEY = "jacksoning";

	public static final int RESPAWN_TIME_SPAWNED = -1;

	private static final ContainerCodecHelper<PlayerData> CODEC = ContainerCodecHelper.by(List.of(
		containerField(
			MethodHandles.lookup(),
			"respawnTime",
			NBT_INT.or(RESPAWN_TIME_SPAWNED),
			"respawn_time"),
		containerField(
			MethodHandles.lookup(),
			"roleState",
			RoleState.CODEC,
			"role_state")
	));

	public PlayerRole initRole = PlayerRole.None;
	public RoleState roleState = new RoleState.None();

	public PlayerRole role() {
		return roleState.role();
	}

	public int respawnTime = RESPAWN_TIME_SPAWNED;

	public boolean isSpawned() {
		return respawnTime == RESPAWN_TIME_SPAWNED;
	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		CODEC.write(this, nbt);
	}

	@Override
	public void readNbt(NbtCompound nbt) throws CodecException {
		CODEC.read(nbt, this);
	}
}
