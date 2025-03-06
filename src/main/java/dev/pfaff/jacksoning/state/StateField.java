package dev.pfaff.jacksoning.state;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.pfaff.jacksoning.server.GameStateInner;
import dev.pfaff.jacksoning.util.codec.Codec;
import dev.pfaff.jacksoning.util.nbt.ContainerCodecHelper;
import dev.pfaff.jacksoning.util.nbt.NbtElement;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public record StateField<T>(
	String field,
	Codec<T, NbtElement> codec,
	T defaultValue,
	ContainerCodecHelper.ContainerField<GameStateInner, T> containerField,
	@Nullable ConfigProps<T, ?> configProps
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
		return configurable(argumentType, clazz, (__, v) -> v);
	}

	public <A> StateField<T> configurable(ArgumentType<A> argumentType, Class<A> clazz, BiFunction<ServerCommandSource, A, T> adapter) {
		return new StateField<>(field, codec, defaultValue, containerField, new ConfigProps<>(argumentType, clazz, adapter));
	}

	public ContainerCodecHelper.ContainerField<GameStateInner, NbtElement> buildContainerField() {
		return containerField.then(codec.or(defaultValue));
	}

	public StateField<T> mapContainerField(UnaryOperator<ContainerCodecHelper.ContainerField<GameStateInner, T>> mapper) {
		return new StateField<>(field,
								codec,
								defaultValue,
								mapper.apply(containerField),
								configProps);
	}

	public record ConfigProps<T, A>(ArgumentType<A> argumentType, Class<A> clazz, BiFunction<ServerCommandSource, A, T> adapter) {
		public T get(CommandContext<ServerCommandSource> context) {
			return adapter.apply(context.getSource(), context.getArgument("value", clazz));
		}
	}
}
