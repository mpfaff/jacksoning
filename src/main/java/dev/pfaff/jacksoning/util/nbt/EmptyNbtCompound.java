package dev.pfaff.jacksoning.util.nbt;

import dev.pfaff.jacksoning.util.codec.CodecException;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

final class EmptyNbtCompound implements NbtCompound {
	static final EmptyNbtCompound INSTANCE = new EmptyNbtCompound();

	private EmptyNbtCompound() {}

	@Override
	public @Nullable NbtElement get(String key) {
		return null;
	}

	@Override
	public void put(String key, NbtElement value) {
		throw new UnsupportedOperationException("immutable");
	}

	@Override
	public NbtCompound getOrPutCompound(String key) throws CodecException {
		throw new UnsupportedOperationException("immutable");
	}

	@Override
	public Iterable<String> keys() {
		return Set.of();
	}
}
