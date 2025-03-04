package dev.pfaff.jacksoning.server;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.pfaff.jacksoning.config.StateField;
import dev.pfaff.jacksoning.util.codec.CodecException;
import dev.pfaff.jacksoning.util.nbt.Container;
import dev.pfaff.jacksoning.util.nbt.ContainerCodecHelper;
import dev.pfaff.jacksoning.util.nbt.NbtCompound;
import dev.pfaff.jacksoning.util.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.PersistentState;

import java.lang.invoke.MethodHandles;
import java.util.List;

import static dev.pfaff.jacksoning.server.GameState.TIME_NOT_STARTED;
import static dev.pfaff.jacksoning.util.nbt.NbtCodecs.NBT_BOOL;
import static dev.pfaff.jacksoning.util.nbt.NbtCodecs.NBT_INT;
import static dev.pfaff.jacksoning.util.nbt.NbtCodecs.NBT_LONG;

public final class GameStateInner implements Container {
	private static final long INIT_TIME = TIME_NOT_STARTED;
	private static final int INIT_GROOVE_GIFTS = 0;
	private static final int INIT_ECONOMY = 1;
	//private static final List<BlockPos> INIT_ZONE_BEACONS = List.of();

	private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

	public static final List<StateField<?>> FIELDS = List.of(
		new StateField<>(LOOKUP, "devMode", NBT_BOOL, false).configurable(BoolArgumentType.bool(), boolean.class),
		new StateField<>(LOOKUP, "spawnDelayMJ", NBT_INT, 20 * 30 * 5).configurable(IntegerArgumentType.integer(0), int.class),
		new StateField<>(LOOKUP, "respawnCooldown", NBT_INT, 20 * 15).configurable(IntegerArgumentType.integer(0), int.class),
		new StateField<>(LOOKUP, "initialEmeraldsMJ", NBT_INT, 12).configurable(IntegerArgumentType.integer(0), int.class),
		new StateField<>(LOOKUP, "initialEmeraldsUN", NBT_INT, 12).configurable(IntegerArgumentType.integer(0), int.class),

		new StateField<>(LOOKUP, "time", NBT_LONG, INIT_TIME),
		new StateField<>(LOOKUP, "grooveGifts", NBT_INT, INIT_GROOVE_GIFTS),
		new StateField<>(LOOKUP, "economy", NBT_INT, INIT_ECONOMY)
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

	private long time;
	private int grooveGifts;
	private int economy;
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

	public void init() {
		time(INIT_TIME);
		grooveGifts(INIT_GROOVE_GIFTS);
		economy(INIT_ECONOMY);
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

	public long time() {
		return time;
	}

	public void time(long time) {
		if (time != this.time) {
			persistentState.markDirty();
			this.time = time;
		}
	}

	public int grooveGifts() {
		return grooveGifts;
	}

	public void grooveGifts(int grooveGifts) {
		if (grooveGifts != this.grooveGifts) {
			persistentState.markDirty();
			this.grooveGifts = grooveGifts;
		}
	}

	public int economy() {
		return economy;
	}

	public void economy(int economy) {
		if (economy != this.economy) {
			persistentState.markDirty();
			this.economy = economy;
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
