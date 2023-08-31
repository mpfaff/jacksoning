package dev.pfaff.jacksoning.server;

import dev.pfaff.jacksoning.Config;
import dev.pfaff.jacksoning.util.nbt.ContainerCodecHelper;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;

import java.lang.invoke.MethodHandles;
import java.util.List;

import static dev.pfaff.jacksoning.server.GameState.TIME_NOT_STARTED;
import static dev.pfaff.jacksoning.util.nbt.Codec.NBT_BOOL;
import static dev.pfaff.jacksoning.util.nbt.Codec.NBT_INT;
import static dev.pfaff.jacksoning.util.nbt.Codec.NBT_LONG;
import static dev.pfaff.jacksoning.util.nbt.ContainerCodecHelper.containerField;

public final class GameStateInner extends PersistentState {
	private static final long INIT_TIME = TIME_NOT_STARTED;
	private static final int INIT_GROOVE_GIFTS = 0;
	private static final int INIT_ECONOMY = 1;
	//private static final List<BlockPos> INIT_ZONE_BEACONS = List.of();

	private static final ContainerCodecHelper<GameStateInner> CODEC = ContainerCodecHelper.by(List.of(
		ContainerCodecHelper.containerField(MethodHandles.lookup(), "time", NBT_LONG, "time", () -> INIT_TIME),
		ContainerCodecHelper.containerField(MethodHandles.lookup(), "grooveGifts", NBT_INT, "grooveGifts", () -> INIT_GROOVE_GIFTS),
		ContainerCodecHelper.containerField(MethodHandles.lookup(), "economy", NBT_INT, "economy", () -> INIT_ECONOMY),
		//ContainerCodec.containerField(MethodHandles.lookup(), "zoneBeacons", NBT_FLAT_BLOCK_POS_LIST, "zone_beacons", () -> INIT_ZONE_BEACONS),
		containerField(__ -> Config.devMode(), (__, b) -> Config.devMode(b), NBT_BOOL, "dev_mode", () -> false)
	));

	private long time;
	private int grooveGifts;
	private int economy;
	//private List<BlockPos> zoneBeacons = List.of();

	public void init() {
		time(INIT_TIME);
		grooveGifts(INIT_GROOVE_GIFTS);
		economy(INIT_ECONOMY);
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		CODEC.write(this, nbt);
		return nbt;
	}

	public void readNbt(NbtCompound nbt) {
		CODEC.read(nbt, this);
		setDirty(false);
	}

	public long time() {
		return time;
	}

	public void time(long time) {
		if (time != this.time) {
			markDirty();
			this.time = time;
		}
	}

	public int grooveGifts() {
		return grooveGifts;
	}

	public void grooveGifts(int grooveGifts) {
		if (grooveGifts != this.grooveGifts) {
			markDirty();
			this.grooveGifts = grooveGifts;
		}
	}

	public int economy() {
		return economy;
	}

	public void economy(int economy) {
		if (economy != this.economy) {
			markDirty();
			this.economy = economy;
		}
	}

	public void devMode(boolean enable) {
		if (enable != Config.devMode()) {
			markDirty();
			Config.devMode(enable);
		}
	}

	//public List<BlockPos> zoneBeacons() {
	//	return zoneBeacons;
	//}
	//
	//public void zoneBeacons(List<BlockPos> zoneBeacons) {
	//	if (!zoneBeacons.equals(this.zoneBeacons)) {
	//		markDirty();
	//		this.zoneBeacons = zoneBeacons;
	//	}
	//}
}
