package dev.pfaff.jacksoning.util.nbt;

import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public record CompoundFieldCodec<T>(Codec<T> codec, String key) {
	public void put(NbtCompound nbt, T value) {
		codec.put(nbt, key, value);
	}

	public T get(NbtCompound nbt, Supplier<T> orElse) {
		return codec.get(nbt, key, orElse);
	}

	@Nullable
	public T getOrNull(NbtCompound nbt) {
		return codec.getOrNull(nbt, key);
	}
}
