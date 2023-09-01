package dev.pfaff.jacksoning.util.nbt;

import org.jetbrains.annotations.Nullable;

public interface NbtCompound extends NbtElement {
	@Nullable
	NbtElement get(String key) throws CodecException;

	default <T> T getAs(String key, Codec<T, NbtElement> codec) throws CodecException {
		return codec.fromR(get(key));
	}

	void put(String key, NbtElement value);

	default <T> void putAs(String key, Codec<T, NbtElement> codec, T value) {
		put(key, codec.toR(value));
	}

	Iterable<String> keys();
}