package dev.pfaff.jacksoning.util.nbt;

import dev.pfaff.jacksoning.util.codec.Codec;
import dev.pfaff.jacksoning.util.codec.CodecException;
import org.jetbrains.annotations.Nullable;

public interface NbtList extends NbtElement {
	int size();

	@Nullable
	NbtElement get(int index);

	default <T> T getAs(int index, Codec<T, NbtElement> codec) throws CodecException {
		return codec.fromR(get(index));
	}

	void set(int index, NbtElement value);

	default <T> void setAs(int index, Codec<T, NbtElement> codec, T value) throws CodecException {
		set(index, codec.toR(value));
	}

	void add(NbtElement value);

	default <T> void addAs(Codec<T, NbtElement> codec, T value) throws CodecException {
		add(codec.toR(value));
	}

	@Override
	default NbtType type() {
		return NbtType.LIST;
	}

	@Override
	default byte asByte() throws CodecException {
		throw new CodecException("Expected a " + NbtType.BYTE + ", found a " + NbtType.LIST);
	}

	@Override
	default short asShort() throws CodecException {
		throw new CodecException("Expected a " + NbtType.SHORT + ", found a " + NbtType.LIST);
	}

	@Override
	default int asInt() throws CodecException {
		throw new CodecException("Expected a " + NbtType.INT + ", found a " + NbtType.LIST);
	}

	@Override
	default long asLong() throws CodecException {
		throw new CodecException("Expected a " + NbtType.LONG + ", found a " + NbtType.LIST);
	}

	@Override
	default float asFloat() throws CodecException {
		throw new CodecException("Expected a " + NbtType.FLOAT + ", found a " + NbtType.LIST);
	}

	@Override
	default double asDouble() throws CodecException {
		throw new CodecException("Expected a " + NbtType.DOUBLE + ", found a " + NbtType.LIST);
	}

	@Override
	default byte[] asByteArray() throws CodecException {
		throw new CodecException("Expected a " + NbtType.BYTE_ARRAY + ", found a " + NbtType.LIST);
	}

	@Override
	default String asString() throws CodecException {
		throw new CodecException("Expected a " + NbtType.STRING + ", found a " + NbtType.LIST);
	}

	@Override
	default NbtList asList() throws CodecException {
		return this;
	}

	@Override
	default NbtCompound asCompound() throws CodecException {
		throw new CodecException("Expected a " + NbtType.COMPOUND + ", found a " + NbtType.LIST);
	}

	@Override
	default int[] asIntArray() throws CodecException {
		throw new CodecException("Expected a " + NbtType.INT_ARRAY + ", found a " + NbtType.LIST);
	}

	@Override
	default long[] asLongArray() throws CodecException {
		throw new CodecException("Expected a " + NbtType.LONG_ARRAY + ", found a " + NbtType.LIST);
	}
}