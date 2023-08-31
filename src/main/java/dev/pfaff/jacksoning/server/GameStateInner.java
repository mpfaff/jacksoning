package dev.pfaff.jacksoning.server;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;

import static dev.pfaff.jacksoning.server.GameState.TIME_NOT_STARTED;

public final class GameStateInner extends PersistentState {
	private static final String NBT_TIME = "time";
	private static final String NBT_GROOVE_GIFTS = "grooveGifts";
	private static final String NBT_ECONOMY = "economy";
	//private static final String NBT_ZONE_BEACONS = "zoneBeacons";

	private long time;
	private int grooveGifts;
	private int economy;
	//private List<BlockPos> zoneBeacons = List.of();

	public void init() {
		time(TIME_NOT_STARTED);
		grooveGifts(0);
		economy(1);
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		nbt.putLong(NBT_TIME, time);
		nbt.putInt(NBT_GROOVE_GIFTS, grooveGifts);
		nbt.putInt(NBT_ECONOMY, economy);
		//var flatZoneBeacons = new int[zoneBeacons.size() * 3];
		//for (int i = 0; i < zoneBeacons.size(); i++) {
		//	flatZoneBeacons[i * 3] = zoneBeacons.get(i).getX();
		//	flatZoneBeacons[i * 3 + 1] = zoneBeacons.get(i).getY();
		//	flatZoneBeacons[i * 3 + 2] = zoneBeacons.get(i).getZ();
		//}
		//nbt.putIntArray(NBT_ZONE_BEACONS, flatZoneBeacons);
		return nbt;
	}

	public void readNbt(NbtCompound nbt) {
		time = nbt.getLong(NBT_TIME);
		grooveGifts = nbt.getInt(NBT_GROOVE_GIFTS);
		economy = nbt.getInt(NBT_ECONOMY);
		//var flatZoneBeacons = nbt.getIntArray(NBT_ZONE_BEACONS);
		//var zoneBeacons = new BlockPos[flatZoneBeacons.length / 3];
		//for (int i = 0; i < zoneBeacons.length; i++) {
		//	zoneBeacons[i] = new BlockPos(flatZoneBeacons[i * 3], flatZoneBeacons[i * 3 + 1], flatZoneBeacons[i * 3 + 2]);
		//}
		//this.zoneBeacons = List.of(zoneBeacons);
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
