package dev.pfaff.jacksoning.server;

import dev.pfaff.jacksoning.PlayerRole;
import dev.pfaff.jacksoning.util.nbt.Container;
import dev.pfaff.jacksoning.util.nbt.ContainerCodecHelper;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.lang.invoke.MethodHandles;
import java.util.List;

import static dev.pfaff.jacksoning.util.nbt.Codec.NBT_BOOL;
import static dev.pfaff.jacksoning.util.nbt.Codec.NBT_COMPOUND;
import static dev.pfaff.jacksoning.util.nbt.Codec.NBT_STRING;

public abstract sealed class RoleState implements Container {
	public static final String NBT_ROLE = "role";

	public abstract PlayerRole role();

	@MustBeInvokedByOverriders
	@Override
	public void readNbt(NbtCompound nbt) {
		assert(role().id.equals(nbt.getString(NBT_ROLE)));
	}

	@MustBeInvokedByOverriders
	@Override
	public void writeNbt(NbtCompound nbt) {
		NBT_STRING.put(nbt, NBT_ROLE, this.role().id);
	}

	public static RoleState fromNbt(NbtCompound nbt) {
		if (!(NBT_STRING.getOrNull(nbt, NBT_ROLE) instanceof String roleStr)) {
			JacksoningServer.LOGGER.error("Invalid role state: " + nbt);
			return PlayerRole.DEFAULT.newState();
		}
		var role = switch (roleStr) {
			case "none" -> PlayerRole.None;
			case "un_leader" -> PlayerRole.UNLeader;
			case "jackson" -> PlayerRole.Jackson;
			case "mistress" -> PlayerRole.Mistress;
			case "referee" -> PlayerRole.Referee;
			default -> null;
		};
		if (role == null) {
			JacksoningServer.LOGGER.error("Invalid player role: " + roleStr);
			return PlayerRole.DEFAULT.newState();
		}
		var state = role.newState();
		state.readNbt(nbt);
		return state;
	}

	public static final class None extends RoleState {
		@Override
		public PlayerRole role() {
			return PlayerRole.None;
		}
	}

	public static final class UNLeader extends RoleState {
		@Override
		public PlayerRole role() {
			return PlayerRole.UNLeader;
		}
	}

	public static final class Jackson extends RoleState {
		private static final ContainerCodecHelper<Jackson> CODEC = ContainerCodecHelper.by(List.of(ContainerCodecHelper.containerField(
			MethodHandles.lookup(),
			"spawned",
			NBT_BOOL,
			"spawned",
			() -> false)));

		public boolean spawned;

		@Override
		public PlayerRole role() {
			return PlayerRole.Jackson;
		}

		@Override
		public void writeNbt(NbtCompound nbt) {
			super.writeNbt(nbt);
			CODEC.write(this, nbt);
		}

		@Override
		public void readNbt(NbtCompound nbt) {
			super.readNbt(nbt);
			CODEC.read(nbt, this);
		}
	}

	public static final class Mistress extends RoleState {
		@Override
		public PlayerRole role() {
			return PlayerRole.Mistress;
		}
	}

	public static final class Referee extends RoleState {
		@Override
		public PlayerRole role() {
			return PlayerRole.Referee;
		}
	}
}
