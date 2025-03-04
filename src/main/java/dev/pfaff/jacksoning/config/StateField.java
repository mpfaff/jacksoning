package dev.pfaff.jacksoning.config;

import com.mojang.brigadier.arguments.ArgumentType;
import dev.pfaff.jacksoning.server.GameStateInner;
import dev.pfaff.jacksoning.util.codec.Codec;
import dev.pfaff.jacksoning.util.nbt.ContainerCodecHelper;
import dev.pfaff.jacksoning.util.nbt.NbtElement;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;


public record StateField<T>(
	String field,
	Codec<T, NbtElement> codec,
	T defaultValue,
	ContainerCodecHelper.ContainerField<GameStateInner, T> containerField,
	@Nullable ConfigProps<T> configProps
) {
	public StateField(
		MethodHandles.Lookup lookup,
		String field,
		Codec<T, NbtElement> codec,
		T defaultValue
	) {
		this(field, codec, defaultValue, ContainerCodecHelper.<GameStateInner, T>containerField(lookup, field).build(), null);
	}

	public StateField<T> configurable(ArgumentType<T> argumentType, Class<T> clazz) {
		return new StateField<>(field, codec, defaultValue, containerField, new ConfigProps<>(argumentType, clazz));
	}

	public ContainerCodecHelper.ContainerField<GameStateInner, NbtElement> buildContainerField() {
		return containerField.then(codec.or(defaultValue));
	}

	public record ConfigProps<T>(ArgumentType<T> argumentType, Class<T> clazz) {}
}
