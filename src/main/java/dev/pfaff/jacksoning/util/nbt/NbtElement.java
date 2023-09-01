package dev.pfaff.jacksoning.util.nbt;

import java.util.List;

/**
 * A better representation of an NBT element. Supports wrapping the existing Minecraft NBT elements and providing
 * mutable views of binary NBT data.
 */
public interface NbtElement {
	static final List<String> TYPE_NAMES = List.of("END", // END_TYPE = 0
												   "BYTE", // BYTE_TYPE = 1
												   "SHORT", // SHORT_TYPE = 2
												   "INT", // INT_TYPE = 3
												   "LONG", // LONG_TYPE = 4
												   "FLOAT", // FLOAT_TYPE = 5
												   "DOUBLE", // DOUBLE_TYPE = 6
												   "BYTE_ARRAY", // BYTE_ARRAY_TYPE = 7
												   "STRING", // STRING_TYPE = 8
												   "LIST", // LIST_TYPE = 9
												   "COMPOUND", // COMPOUND_TYPE = 10
												   "INT_ARRAY", // INT_ARRAY_TYPE = 11
												   "LONG_ARRAY" // LONG_ARRAY_TYPE = 12
	);

	byte type();

	default <T> T as(Codec<T, NbtElement> codec) throws CodecException {
		return codec.fromR(this);
	}

	byte asByte();
	short asShort();
	int asInt();
	long asLong();
	float asFloat();
	double asDouble();
	byte[] asByteArray();
	String asString();
	NbtList asList();
	NbtCompound asCompound();
	int[] asIntArray();
	long[] asLongArray();

	static NbtElement of(net.minecraft.nbt.NbtElement nbt) {
		return MinecraftNbtWrapper.of(nbt);
	}

	static NbtCompound of(net.minecraft.nbt.NbtCompound nbt) {
		return MinecraftNbtWrapper.of(nbt);
	}

	static NbtList of(net.minecraft.nbt.NbtList nbt) {
		return MinecraftNbtWrapper.of(nbt);
	}

	static NbtCompound compound() {
		return of(new net.minecraft.nbt.NbtCompound());
	}

	static NbtList list() {
		return of(new net.minecraft.nbt.NbtList());
	}
}
