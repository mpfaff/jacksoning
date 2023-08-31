package dev.pfaff.jacksoning.server;

import dev.pfaff.jacksoning.PlayerRole;
import dev.pfaff.jacksoning.util.nbt.Container;
import dev.pfaff.jacksoning.util.nbt.ContainerCodecHelper;
import net.minecraft.nbt.NbtCompound;

import java.lang.invoke.MethodHandles;
import java.util.List;

import static dev.pfaff.jacksoning.util.nbt.Codec.NBT_COMPOUND;
import static dev.pfaff.jacksoning.util.nbt.Codec.NBT_INT;

public final class PlayerData implements Container {
	public static final String PLAYER_NBT_KEY = "jacksoning";
	public static final String NBT_ROLE_STATE = "role_state";

	public static final int RESPAWN_TIME_SPAWNED = -1;

	private static final ContainerCodecHelper<PlayerData> CODEC = ContainerCodecHelper.by(List.of(ContainerCodecHelper.containerField(
		MethodHandles.lookup(),
		"respawnTime",
		NBT_INT,
		"respawn_time",
		() -> RESPAWN_TIME_SPAWNED)));

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
		NBT_COMPOUND.put(nbt, NBT_ROLE_STATE, roleState.writeNbt());
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		CODEC.read(nbt, this);
		if (NBT_COMPOUND.getOrNull(nbt, NBT_ROLE_STATE) instanceof NbtCompound inner) {
			roleState = RoleState.fromNbt(inner);
		} else {
			JacksoningServer.LOGGER.error("Invalid role state: " + nbt.get(NBT_ROLE_STATE));
			roleState = PlayerRole.DEFAULT.newState();
		}
	}
}
