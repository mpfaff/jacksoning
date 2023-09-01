package dev.pfaff.jacksoning.server;

import dev.pfaff.jacksoning.Config;
import dev.pfaff.jacksoning.util.codec.CodecException;
import dev.pfaff.jacksoning.util.nbt.Container;
import dev.pfaff.jacksoning.util.nbt.ContainerCodecHelper;
import dev.pfaff.jacksoning.util.nbt.NbtCompound;
import dev.pfaff.jacksoning.util.nbt.NbtElement;
import net.minecraft.world.PersistentState;

import java.lang.invoke.MethodHandles;
import java.util.List;

import static dev.pfaff.jacksoning.server.GameState.TIME_NOT_STARTED;
import static dev.pfaff.jacksoning.util.nbt.NbtCodecs.NBT_BOOL;
import static dev.pfaff.jacksoning.util.nbt.NbtCodecs.NBT_INT;
import static dev.pfaff.jacksoning.util.nbt.NbtCodecs.NBT_LONG;
import static dev.pfaff.jacksoning.util.nbt.ContainerCodecHelper.containerField;

public final class GameStateInner implements Container {
	private static final long INIT_TIME = TIME_NOT_STARTED;
	private static final int INIT_GROOVE_GIFTS = 0;
	private static final int INIT_ECONOMY = 1;
	//private static final List<BlockPos> INIT_ZONE_BEACONS = List.of();

	private static final ContainerCodecHelper<GameStateInner> CODEC = ContainerCodecHelper.by(List.of(
		containerField(MethodHandles.lookup(), "time", NBT_LONG.or(INIT_TIME), "time"),
		containerField(MethodHandles.lookup(), "grooveGifts", NBT_INT.or(INIT_GROOVE_GIFTS), "grooveGifts"),
		containerField(MethodHandles.lookup(), "economy", NBT_INT.or(INIT_ECONOMY), "economy"),
		//containerField(MethodHandles.lookup(), "zoneBeacons", NBT_FLAT_BLOCK_POS_LIST.or(INIT_ZONE_BEACONS), "zone_beacons"),
		containerField(__ -> Config.devMode(), (__, b) -> Config.devMode(b), NBT_BOOL.or(false), "dev_mode")
	));

	private long time;
	private int grooveGifts;
	private int economy;
	//private List<BlockPos> zoneBeacons = List.of();

	public final PersistentState persistentState = new PersistentState() {
		@Override
		public net.minecraft.nbt.NbtCompound writeNbt(net.minecraft.nbt.NbtCompound nbt) {
			GameStateInner.this.writeNbt(NbtElement.of(nbt));
			return nbt;
		}
	};

	public void init() {
		time(INIT_TIME);
		grooveGifts(INIT_GROOVE_GIFTS);
		economy(INIT_ECONOMY);
	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		CODEC.write(this, nbt);
	}

	public void readNbt(NbtCompound nbt) throws CodecException {
		CODEC.read(nbt, this);
		persistentState.setDirty(false);
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

	public void devMode(boolean enable) {
		if (enable != Config.devMode()) {
			persistentState.markDirty();
			Config.devMode(enable);
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
