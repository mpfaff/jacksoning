package dev.pfaff.jacksoning.util.codec;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Codecs {
	public static <T extends Enum<T>> Codec<T, String> enumAsString(Class<T> clazz, Function<T, String> id) {
		var byName = enumByNameMap(clazz, id);
		var errorMsg = "Expected one of " + List.copyOf(byName.values()) + ", found ";
		return Codec.by(Coder.adapt(id), r -> {
			var t = byName.get(r);
			if (t == null) throw new CodecException(errorMsg + r);
			return t;
		});
	}

	public static <T extends Enum<T>> @NotNull Map<@NotNull String, T> enumByNameMap(Class<T> clazz,
																					 Function<T, String> id) {
		return Arrays.stream(clazz.getEnumConstants())
					 .collect(Collectors.toUnmodifiableMap(id, Function.identity()));
	}

	public static <T extends Enum<T>> Codec<T, Integer> enumAsInt(Class<T> clazz, Function<T, Integer> id) {
		var values = List.of(clazz.getEnumConstants());
		return Codec.by(Coder.adapt(id), r -> {
			if (r < 0 || r >= values.size()) {
				throw new CodecException("Expected a value in the range [0, " + values.size() + "), found " + r);
			}
			return values.get(r);
		});
	}
}
