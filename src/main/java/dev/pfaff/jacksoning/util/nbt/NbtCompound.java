package dev.pfaff.jacksoning.util.nbt;

import dev.pfaff.jacksoning.util.codec.Codec;
import dev.pfaff.jacksoning.util.codec.CodecException;
import org.jetbrains.annotations.Nullable;

public interface NbtCompound extends NbtElement {
	/**
	 * An immutable empty compound.
	 */
	static NbtCompound empty() {
		return EmptyNbtCompound.INSTANCE;
	}

	@Nullable
	NbtElement get(String key);

	default <T> T getAs(String key, Codec<T, NbtElement> codec) throws CodecException {
		return codec.fromR(get(key));
	}

	void put(String key, NbtElement value);

	default <T> void putAs(String key, Codec<T, NbtElement> codec, T value) throws CodecException {
		put(key, codec.toR(value));
	}

	NbtCompound getOrPutCompound(String key) throws CodecException;

	Iterable<String> keys();

	@Override
	default NbtType type() {
		return NbtType.COMPOUND;
	}

	@Override
	default byte asByte() throws CodecException {
		throw new CodecException("Expected a " + NbtType.BYTE + ", found a " + NbtType.COMPOUND);
	}

	@Override
	default short asShort() throws CodecException {
		throw new CodecException("Expected a " + NbtType.SHORT + ", found a " + NbtType.COMPOUND);
	}

	@Override
	default int asInt() throws CodecException {
		throw new CodecException("Expected a " + NbtType.INT + ", found a " + NbtType.COMPOUND);
	}

	@Override
	default long asLong() throws CodecException {
		throw new CodecException("Expected a " + NbtType.LONG + ", found a " + NbtType.COMPOUND);
	}

	@Override
	default float asFloat() throws CodecException {
		throw new CodecException("Expected a " + NbtType.FLOAT + ", found a " + NbtType.COMPOUND);
	}

	@Override
	default double asDouble() throws CodecException {
		throw new CodecException("Expected a " + NbtType.DOUBLE + ", found a " + NbtType.COMPOUND);
	}

	@Override
	default byte[] asByteArray() throws CodecException {
		throw new CodecException("Expected a " + NbtType.BYTE_ARRAY + ", found a " + NbtType.COMPOUND);
	}

	@Override
	default String asString() throws CodecException {
		throw new CodecException("Expected a " + NbtType.STRING + ", found a " + NbtType.COMPOUND);
	}

	@Override
	default NbtList asList() throws CodecException {
		throw new CodecException("Expected a " + NbtType.LIST + ", found a " + NbtType.COMPOUND);
	}

	@Override
	default NbtCompound asCompound() throws CodecException {
		return this;
	}

	@Override
	default int[] asIntArray() throws CodecException {
		throw new CodecException("Expected a " + NbtType.INT_ARRAY + ", found a " + NbtType.COMPOUND);
	}

	@Override
	default long[] asLongArray() throws CodecException {
		throw new CodecException("Expected a " + NbtType.LONG_ARRAY + ", found a " + NbtType.COMPOUND);
	}
}