package dev.pfaff.jacksoning.util.nbt;

import dev.pfaff.jacksoning.util.codec.Codec;
import dev.pfaff.jacksoning.util.codec.CodecException;

import java.util.List;

/**
 * A better representation of an NBT element. Supports wrapping the existing Minecraft NBT elements and providing
 * mutable views of binary NBT data.
 */
public interface NbtElement {
	@Deprecated
	static final List<String> TYPE_NAMES = NbtType.NAMES;

	@Deprecated
	static boolean isTypeValid(byte type) {
		return NbtType.isValid(type);
	}

	@Deprecated
	static String nameOf(byte type) {
		return NbtType.nameOf(type);
	}

	NbtType type();

	default <T> T as(Codec<T, NbtElement> codec) throws CodecException {
		return codec.fromR(this);
	}

	byte asByte() throws CodecException;
	short asShort() throws CodecException;
	int asInt() throws CodecException;
	long asLong() throws CodecException;
	float asFloat() throws CodecException;
	double asDouble() throws CodecException;
	byte[] asByteArray() throws CodecException;
	String asString() throws CodecException;
	NbtList asList() throws CodecException;
	NbtCompound asCompound() throws CodecException;
	int[] asIntArray() throws CodecException;
	long[] asLongArray() throws CodecException;

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
