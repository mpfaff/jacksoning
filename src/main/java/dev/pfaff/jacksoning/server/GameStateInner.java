package dev.pfaff.jacksoning.server;

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
import static dev.pfaff.jacksoning.util.nbt.ContainerCodecHelper.containerField;
import static dev.pfaff.jacksoning.util.nbt.NbtCodecs.NBT_BOOL;
import static dev.pfaff.jacksoning.util.nbt.NbtCodecs.NBT_INT;
import static dev.pfaff.jacksoning.util.nbt.NbtCodecs.NBT_LONG;

public final class GameStateInner implements Container {
	private static final long INIT_TIME = TIME_NOT_STARTED;
	private static final int INIT_GROOVE_GIFTS = 0;
	private static final int INIT_ECONOMY = 1;
	//private static final List<BlockPos> INIT_ZONE_BEACONS = List.of();

	private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

	private static final ContainerCodecHelper<GameStateInner> CODEC = ContainerCodecHelper.by(List.of(
		containerField(LOOKUP, "devMode", NBT_BOOL.or(false)),

		containerField(LOOKUP, "time", NBT_LONG.or(INIT_TIME)),
		containerField(LOOKUP, "grooveGifts", NBT_INT.or(INIT_GROOVE_GIFTS)),
		containerField(LOOKUP, "economy", NBT_INT.or(INIT_ECONOMY))
		//containerField(LOOKUP, "zoneBeacons", NBT_FLAT_BLOCK_POS_LIST.or(INIT_ZONE_BEACONS), "zone_beacons"),
	));

	private boolean devMode;

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
