package dev.pfaff.jacksoning.server;

import dev.pfaff.jacksoning.PlayerRole;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

public abstract sealed class PlayerState {
	private static final String PLAYER_NBT_KEY = "jacksoning_state";
	private static final String NBT_ROLE = "role";

	public abstract PlayerRole role();

	public abstract GameMode gameMode();

	public void tick(ServerPlayerEntity player) {
		this.applyGameMode(player);
	}

	public final void applyGameMode(ServerPlayerEntity player) {
		var gm = this.gameMode();
		if (gm != player.interactionManager.getGameMode()) {
			player.changeGameMode(gm);
		}
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
	}

	@MustBeInvokedByOverriders
	public void readNbt(NbtCompound nbt) {
	}

	public final void writeToPlayerNbt(NbtCompound nbt) {
		NbtCompound inner = new NbtCompound();
		writeNbt(inner);
		nbt.put(PLAYER_NBT_KEY, inner);
	}

	public static PlayerState readFromPlayerNbt(NbtCompound nbt) {
		if (nbt.contains(PLAYER_NBT_KEY, NbtElement.COMPOUND_TYPE)) {
			NbtCompound inner = nbt.getCompound(PLAYER_NBT_KEY);
			var role = inner.contains(NBT_ROLE, NbtElement.STRING_TYPE) ? switch (inner.getString(NBT_ROLE)) {
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
