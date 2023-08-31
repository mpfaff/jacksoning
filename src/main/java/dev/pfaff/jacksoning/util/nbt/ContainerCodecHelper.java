package dev.pfaff.jacksoning.util.nbt;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ContainerCodecHelper<T> {
	void read(NbtCompound nbt, T container);

	void write(T container, NbtCompound nbt);

	record CompoundField<T, R>(Function<T, R> getter,
							   BiConsumer<T, R> setter,
							   Codec<R> codec,
							   String key,
							   Function<NbtElement, R> orElse) {}

	static <T, R> CompoundField<T, R> containerField(Function<T, R> getter,
													 BiConsumer<T, R> setter,
													 Codec<R> codec,
													 String key,
													 Function<NbtElement, R> orElse) {
		return new CompoundField<>(getter, setter, codec, key, orElse);
	}

	static <T, R> CompoundField<T, R> containerField(Function<T, R> getter,
													 BiConsumer<T, R> setter,
													 Codec<R> codec,
													 String key,
													 Supplier<R> orElse) {
		return containerField(getter, setter, codec, key, e -> orElse.get());
	}

	static <T, R> CompoundField<T, R> containerField(MethodHandles.Lookup l,
													 String field,
													 Codec<R> codec,
													 String key,
													 Supplier<R> orElse) {
		MethodHandle getter;
		MethodHandle setter;
		try {
			var fieldType = (Class<R>) l.lookupClass().getDeclaredField(field).getType();
			getter = l.findGetter(l.lookupClass(), field, fieldType);
			setter = l.findSetter(l.lookupClass(), field, fieldType);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
		return containerField((T t) -> {
			try {
				return (R) getter.invoke(t);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}, (T t, R r) -> {
			try {
				setter.invoke(t, r);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}, codec, key, orElse);
	}

	static <T, R> CompoundField<T, R> containerTagField(R value,
														Codec<R> codec,
														String key,
														BiPredicate<R, R> equality) {
		return containerField(t -> value, (t, r) -> {
			if (!equality.test(r, value)) {
				throw new CodecException("Expected value of tag field " + key + " to be '" + value + "', found " + r);
			}
		}, codec, key, e -> {
			throw new CodecException("Expected value of tag field " + key + " to be '" + value + "', found " + e);
		});
	}

	static <T> ContainerCodecHelper<T> by(List<CompoundField> fields) {
		return new ContainerCodecHelper<>() {
			@Override
			public void read(NbtCompound nbt, T container) {
				fields.iterator().forEachRemaining(field -> {
					field.setter().accept(container, field.codec().get(nbt, field.key(), field.orElse()));
				});
			}

			@Override
			public void write(T container, NbtCompound nbt) {
				fields.iterator().forEachRemaining(field -> {
					field.codec().put(nbt, field.key(), field.getter().apply(container));
				});
			}
		};
	}
}
