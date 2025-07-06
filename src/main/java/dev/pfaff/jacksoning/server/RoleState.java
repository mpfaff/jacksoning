package dev.pfaff.jacksoning.server;

import dev.pfaff.jacksoning.player.PlayerRole;
import dev.pfaff.jacksoning.server.shop.ShopItems;
import dev.pfaff.jacksoning.server.shop.ShopState;
import dev.pfaff.jacksoning.util.codec.Codec;
import dev.pfaff.jacksoning.util.codec.CodecException;
import dev.pfaff.jacksoning.util.nbt.Container;
import dev.pfaff.jacksoning.util.nbt.ContainerCodecHelper;
import dev.pfaff.jacksoning.util.nbt.NbtCompound;
import dev.pfaff.jacksoning.util.nbt.NbtElement;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;
import org.slf4j.event.Level;

import java.lang.invoke.MethodHandles;
import java.util.List;

import static dev.pfaff.jacksoning.Jacksoning.LOGGER;
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
		LOGGER.log(Level.ERROR, "Invalid role state", e);
		return PlayerRole.DEFAULT.newState();
	});

	public final PlayerRole role() {
		return switch (this) {
			case None ignored -> PlayerRole.None;
			case Jackson ignored -> PlayerRole.Jackson;
			case Mistress ignored -> PlayerRole.Mistress;
			case UNLeader ignored -> PlayerRole.UNLeader;
			case Referee ignored -> PlayerRole.Referee;
		};
	}

	@Nullable
	public final ShopState shop() {
		if (this instanceof Jackson jackson) {
			return jackson.shop;
		} else if (this instanceof Mistress mistress) {
			return mistress.shop;
		} else {
			return null;
		}
	}

	@MustBeInvokedByOverriders
	@Override
	public void readNbt(NbtCompound nbt) throws CodecException {
		assert(role() == nbt.getAs(NBT_ROLE, PlayerRole.NBT_CODEC));
	}

	@MustBeInvokedByOverriders
	@Override
	public void writeNbt(NbtCompound nbt) throws CodecException {
		nbt.putAs(NBT_ROLE, PlayerRole.NBT_CODEC, this.role());
	}

	public static final class None extends RoleState {
	}

	public static final class UNLeader extends RoleState {
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
		public final ShopState shop = new ShopState(ShopItems.JACKSON);

		@Override
		public void writeNbt(NbtCompound nbt) throws CodecException {
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

		public final ShopState shop = new ShopState(ShopItems.MISTRESS);

		@Override
		public void writeNbt(NbtCompound nbt) throws CodecException {
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
	}
}
