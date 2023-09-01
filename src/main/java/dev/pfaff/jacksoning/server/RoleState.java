package dev.pfaff.jacksoning.server;

import dev.pfaff.jacksoning.PlayerRole;
import dev.pfaff.jacksoning.server.shop.Shop;
import dev.pfaff.jacksoning.server.shop.ShopItems;
import dev.pfaff.jacksoning.util.codec.Codec;
import dev.pfaff.jacksoning.util.codec.CodecException;
import dev.pfaff.jacksoning.util.nbt.Container;
import dev.pfaff.jacksoning.util.nbt.ContainerCodecHelper;
import dev.pfaff.jacksoning.util.nbt.NbtCompound;
import dev.pfaff.jacksoning.util.nbt.NbtElement;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.lang.invoke.MethodHandles;
import java.util.List;

import static dev.pfaff.jacksoning.util.nbt.ContainerCodecHelper.containerField;
import static dev.pfaff.jacksoning.util.nbt.ContainerCodecHelper.containerFieldInPlace;
import static dev.pfaff.jacksoning.util.nbt.NbtCodecs.NBT_BOOL;
import static dev.pfaff.jacksoning.util.nbt.NbtCodecs.NBT_COMPOUND;

public abstract sealed class RoleState implements Container {
	public static final String NBT_ROLE = "role";

	public static final Codec<RoleState, NbtElement> CODEC = NBT_COMPOUND.then(Codec.by((RoleState t) -> {
		var nbt = NbtElement.compound();
		t.writeNbt(nbt);
		return nbt;
	}, r -> {
		var role = r.getAs(NBT_ROLE, PlayerRole.NBT_CODEC);
		var state = role.newState();
		state.readNbt(r);
		return state;
	})).orElse((e, r) -> {
		JacksoningServer.LOGGER.error("Invalid role state", e);
		return PlayerRole.DEFAULT.newState();
	});

	public abstract PlayerRole role();

	@MustBeInvokedByOverriders
	@Override
	public void readNbt(NbtCompound nbt) throws CodecException {
		assert(role() == nbt.getAs(NBT_ROLE, PlayerRole.NBT_CODEC));
	}

	@MustBeInvokedByOverriders
	@Override
	public void writeNbt(NbtCompound nbt) {
		nbt.putAs(NBT_ROLE, PlayerRole.NBT_CODEC, this.role());
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
		private static final ContainerCodecHelper<Jackson> CODEC = ContainerCodecHelper.by(List.of(
			containerField(
				MethodHandles.lookup(),
				"spawned",
				NBT_BOOL.or(false),
				"spawned"),
			containerFieldInPlace((Jackson t) -> t.shop, "shop").orElse((e, t, r) -> t.shop.reset())
		));

		public boolean spawned;
		public final Shop shop = new Shop(ShopItems.JACKSON);

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
		public void readNbt(NbtCompound nbt) throws CodecException {
			super.readNbt(nbt);
			CODEC.read(nbt, this);
		}
	}

	public static final class Mistress extends RoleState {
		private static final ContainerCodecHelper<Mistress> CODEC = ContainerCodecHelper.by(List.of(
			containerFieldInPlace((Mistress t) -> t.shop, "shop").orElse((e, t, r) -> t.shop.reset())
		));

		public final Shop shop = new Shop(ShopItems.MISTRESS);

		@Override
		public PlayerRole role() {
			return PlayerRole.Mistress;
		}

		@Override
		public void writeNbt(NbtCompound nbt) {
			super.writeNbt(nbt);
			CODEC.write(this, nbt);
		}

		@Override
		public void readNbt(NbtCompound nbt) throws CodecException {
			super.readNbt(nbt);
			CODEC.read(nbt, this);
		}
	}

	public static final class Referee extends RoleState {
		@Override
		public PlayerRole role() {
			return PlayerRole.Referee;
		}
	}
}
