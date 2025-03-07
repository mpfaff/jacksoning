package dev.pfaff.jacksoning.server;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.pfaff.jacksoning.state.StateField;
import dev.pfaff.jacksoning.util.codec.CodecException;
import dev.pfaff.jacksoning.util.nbt.Container;
import dev.pfaff.jacksoning.util.nbt.ContainerCodecHelper;
import dev.pfaff.jacksoning.util.nbt.NbtCompound;
import dev.pfaff.jacksoning.util.nbt.NbtElement;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.PersistentState;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static dev.pfaff.jacksoning.server.GameState.TIME_NOT_STARTED;
import static dev.pfaff.jacksoning.util.nbt.NbtCodecs.NBT_BOOL;
import static dev.pfaff.jacksoning.util.nbt.NbtCodecs.NBT_INT;
import static dev.pfaff.jacksoning.util.nbt.NbtCodecs.NBT_LONG;
import static dev.pfaff.jacksoning.util.nbt.NbtCodecs.NBT_VEC3D;

public final class GameStateInner implements Container {
	private static final long INIT_TIME = TIME_NOT_STARTED;
	private static final long INIT_JACKSON_LAST_SEEN = 0L;
	//private static final List<BlockPos> INIT_ZONE_BEACONS = List.of();

	private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

	public static final List<StateField<?>> FIELDS = List.of(
		new StateField<>(LOOKUP, "devMode", NBT_BOOL, false).configurable(BoolArgumentType.bool(), boolean.class),
		new StateField<>(LOOKUP, "spawnDelayMJ", NBT_INT, 20 * 30 * 5).configurable(IntegerArgumentType.integer(0), int.class),
		new StateField<>(LOOKUP, "respawnCooldown", NBT_INT, 20 * 15).configurable(IntegerArgumentType.integer(0), int.class),
		new StateField<>(LOOKUP, "initialEmeraldsMJ", NBT_INT, 12).configurable(IntegerArgumentType.integer(0), int.class),
		new StateField<>(LOOKUP, "initialEmeraldsUN", NBT_INT, 12).configurable(IntegerArgumentType.integer(0), int.class),
		new StateField<>(LOOKUP, "jacksonTimeout", NBT_INT, 20 * 30).configurable(IntegerArgumentType.integer(0), int.class),
		new StateField<>(LOOKUP, "playSpawnPoint", NBT_VEC3D.skipNulls(), null)
			.mapContainerField(ContainerCodecHelper.ContainerField::skipNulls)
			.configurable(Vec3ArgumentType.vec3(), PosArgument.class, GameStateInner::adaptPosArgumentArgument),

		new StateField<>(LOOKUP, "seed", NBT_LONG, 0L),
		new StateField<>(LOOKUP, "time", NBT_LONG, INIT_TIME),
		new StateField<>(LOOKUP, "jacksonLastSeen", NBT_LONG, INIT_JACKSON_LAST_SEEN)
		//containerField(LOOKUP, "zoneBeacons", NBT_FLAT_BLOCK_POS_LIST.or(INIT_ZONE_BEACONS), "zone_beacons"),
	);

	private static final ContainerCodecHelper<GameStateInner> CODEC = ContainerCodecHelper.by(
		FIELDS.stream()
			  .map(StateField::buildContainerField)
			  .toList()
	);

	private boolean devMode;
	private int spawnDelayMJ;
	private int respawnCooldown;
	private int initialEmeraldsMJ;
	private int initialEmeraldsUN;
	private int jacksonTimeout;
	@Nullable
	private Vec3d playSpawnPoint;

	private long seed;
	private long time;
	private long jacksonLastSeen;
	//private List<BlockPos> zoneBeacons = List.of();

	public final PersistentState persistentState = new PersistentState() {
		@Override
		public net.minecraft.nbt.NbtCompound writeNbt(
			net.minecraft.nbt.NbtCompound nbt,
			RegistryWrapper.WrapperLookup registries
		) {
			try {
				GameStateInner.this.writeNbt(NbtElement.of(nbt));
			} catch (CodecException e) {
				throw new RuntimeException(e);
			}
			return nbt;
		}
	};

	private static Vec3d adaptPosArgumentArgument(ServerCommandSource source, PosArgument pos) {
		return pos.getPos(source);
	}

	public void init() {
		seed = ThreadLocalRandom.current().nextLong();
		time(INIT_TIME);
		jacksonLastSeen(INIT_JACKSON_LAST_SEEN);
	}

	@Override
	public void writeNbt(NbtCompound nbt) throws CodecException {
		CODEC.write(this, nbt);
	}

	public void readNbt(NbtCompound nbt) throws CodecException {
		CODEC.read(nbt, this);
		persistentState.setDirty(false);
	}

	public void resetConfig() throws CodecException {
		CODEC.read(NbtCompound.empty(), this);
		persistentState.setDirty(true);
	}

	/**
	 * Reduces cooldowns, ignores some checks.
	 */
	public boolean devMode() {
		return devMode;
	}

	public void devMode(boolean devMode) {
		if (devMode != this.devMode) {
			persistentState.markDirty();
			this.devMode = devMode;
		}
	}

	public int spawnDelayMJ() {
		return spawnDelayMJ;
	}

	public void spawnDelayMJ(int ticks) {
		if (ticks != this.spawnDelayMJ) {
			persistentState.markDirty();
			this.spawnDelayMJ = ticks;
		}
	}

	public int respawnCooldown() {
		return respawnCooldown;
	}

	public void respawnCooldown(int ticks) {
		if (ticks != this.respawnCooldown) {
			persistentState.markDirty();
			this.respawnCooldown = ticks;
		}
	}

	public int initialEmeraldsMJ() {
		return initialEmeraldsMJ;
	}

	public void initialEmeraldsMJ(int count) {
		if (count != this.initialEmeraldsMJ) {
			persistentState.markDirty();
			this.initialEmeraldsMJ = count;
		}
	}

	public int initialEmeraldsUN() {
		return initialEmeraldsUN;
	}

	public void initialEmeraldsUN(int count) {
		if (count != this.initialEmeraldsUN) {
			persistentState.markDirty();
			this.initialEmeraldsUN = count;
		}
	}

	public int jacksonTimeout() {
		return jacksonTimeout;
	}

	public void jacksonTimeout(int ticks) {
		if (ticks != this.jacksonTimeout) {
			persistentState.markDirty();
			this.jacksonTimeout = ticks;
		}
	}

	@Nullable
	public Vec3d playSpawnPoint() {
		return playSpawnPoint;
	}

	public void playSpawnPoint(@Nullable Vec3d pos) {
		if (!Objects.equals(pos, this.playSpawnPoint)) {
			persistentState.markDirty();
			this.playSpawnPoint = pos;
		}
	}

	public long seed() {
		return seed;
	}

	public long time() {
		return time;
	}

	public void time(long time) {
		if (time != this.time) {
			persistentState.markDirty();
			this.time = time;
		}
	}

	public long jacksonLastSeen() {
		return jacksonLastSeen;
	}

	public void jacksonLastSeen(long time) {
		if (time != this.jacksonLastSeen) {
			persistentState.markDirty();
			this.jacksonLastSeen = time;
		}
	}

	//public List<BlockPos> zoneBeacons() {
	//	return zoneBeacons;
	//}
	//
	//public void zoneBeacons(List<BlockPos> zoneBeacons) {
	//	if (!zoneBeacons.equals(this.zoneBeacons)) {
	//		persistentState.markDirty();
	//		this.zoneBeacons = zoneBeacons;
	//	}
	//}
}
