package dev.pfaff.jacksoning.server;

import dev.pfaff.jacksoning.PlayerRole;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import static dev.pfaff.jacksoning.util.nbt.NbtData.NBT_COMPOUND;
import static dev.pfaff.jacksoning.util.nbt.NbtData.NBT_INT;
import static dev.pfaff.jacksoning.util.nbt.NbtData.NBT_STRING;

public abstract sealed class PlayerState {
	private static final String PLAYER_NBT_KEY = "jacksoning_state";
	private static final String NBT_ROLE = "role";
	private static final String NBT_RESPAWN_TIME = "respawn_time";

	public static final int RESPAWN_TIME_SPAWNED = -1;

	public abstract PlayerRole role();

	public abstract GameMode gameMode();

	public int respawnTime = RESPAWN_TIME_SPAWNED;

	public boolean isSpawned() {
		return respawnTime == RESPAWN_TIME_SPAWNED;
	}

	public static final class None extends PlayerState {
		@Override
		public PlayerRole role() {
			return PlayerRole.None;
		}

		@Override
		public GameMode gameMode() {
			return GameMode.SPECTATOR;
		}
	}

	public static final class UNLeader extends PlayerState {
		@Override
		public PlayerRole role() {
			return PlayerRole.UNLeader;
		}

		@Override
		public GameMode gameMode() {
			return GameMode.SURVIVAL;
		}
	}

	public static final class Jackson extends PlayerState {
		@Override
		public PlayerRole role() {
			return PlayerRole.Jackson;
		}

		@Override
		public GameMode gameMode() {
			return GameMode.SURVIVAL;
		}
	}

	public static final class Mistress extends PlayerState {
		@Override
		public PlayerRole role() {
			return PlayerRole.Mistress;
		}

		@Override
		public GameMode gameMode() {
			return GameMode.SURVIVAL;
		}
	}

	public static final class Referee extends PlayerState {
		@Override
		public PlayerRole role() {
			return PlayerRole.Referee;
		}

		@Override
		public GameMode gameMode() {
			return GameMode.SPECTATOR;
		}
	}

	@MustBeInvokedByOverriders
	public void writeNbt(NbtCompound nbt) {
		nbt.putString(NBT_ROLE, this.role().id);
		nbt.putInt(NBT_RESPAWN_TIME, respawnTime);
	}

	@MustBeInvokedByOverriders
	public void readNbt(NbtCompound nbt) {
		respawnTime = NBT_INT.fromElementOrElse(nbt.get(NBT_RESPAWN_TIME), () -> RESPAWN_TIME_SPAWNED);
	}

	public final void writeToPlayerNbt(NbtCompound nbt) {
		NbtCompound inner = new NbtCompound();
		writeNbt(inner);
		nbt.put(PLAYER_NBT_KEY, inner);
	}

	public static PlayerState readFromPlayerNbt(NbtCompound nbt) {
		if (NBT_COMPOUND.get(nbt, PLAYER_NBT_KEY) instanceof NbtCompound inner) {
			var role = NBT_STRING.get(inner, NBT_ROLE) instanceof String roleStr ? switch (roleStr) {
				case "none" -> PlayerRole.None;
				case "un_leader" -> PlayerRole.UNLeader;
				case "jackson" -> PlayerRole.Jackson;
				case "mistress" -> PlayerRole.Mistress;
				case "referee" -> PlayerRole.Referee;
				default -> {
					JacksoningServer.LOGGER.error("Invalid player role: " + nbt.getString(NBT_ROLE));
					yield null;
				}
			} : null;
			if (role == null) return PlayerRole.DEFAULT.newState();
			var state = role.newState();
			state.readNbt(inner);
			return state;
		} else {
			return PlayerRole.DEFAULT.newState();
		}
	}
}
