package dev.pfaff.jacksoning.util.nbt;

import net.minecraft.nbt.NbtCompound;

public interface Container {
	void readNbt(NbtCompound nbt);

	void writeNbt(NbtCompound nbt);

	default NbtCompound writeNbt() {
		var nbt = new NbtCompound();
		writeNbt(nbt);
		return nbt;
	}
}
