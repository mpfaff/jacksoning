package dev.pfaff.jacksoning.util.nbt;

import dev.pfaff.jacksoning.util.codec.Codec;
import dev.pfaff.jacksoning.util.codec.CodecException;
import org.jetbrains.annotations.Nullable;

public interface NbtList extends NbtElement {
	int size();

	@Nullable
	NbtElement get(int index) throws CodecException;

	default <T> T getAs(int index, Codec<T, NbtElement> codec) throws CodecException {
		return codec.fromR(get(index));
	}

	void set(int index, NbtElement value);

	default <T> void setAs(int index, Codec<T, NbtElement> codec, T value) {
		set(index, codec.toR(value));
	}

	void add(NbtElement value);

	default <T> void addAs(Codec<T, NbtElement> codec, T value) {
		add(codec.toR(value));
	}
}