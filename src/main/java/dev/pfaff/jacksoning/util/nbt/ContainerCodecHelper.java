package dev.pfaff.jacksoning.util.nbt;

import dev.pfaff.jacksoning.util.codec.Codec;
import dev.pfaff.jacksoning.util.codec.CodecException;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;

public interface ContainerCodecHelper<T> {
	void read(NbtCompound nbt, T container) throws CodecException;

	void write(T container, NbtCompound nbt);

	@FunctionalInterface
	interface FieldSetter<T, R> {
		void set(T container, R value) throws CodecException;

		default FieldSetter<T, R> orElse(OrElse<T, R> f) {
			var self = this;
			return (t, r) -> {
				try {
					self.set(t, r);
				} catch (CodecException e) {
					f.set(e, t, r);
				}
			};
		}

		@FunctionalInterface
		public interface OrElse<T, R> {
			void set(CodecException e, T container, R value) throws CodecException;
		}
	}

	record CompoundField<T>(Function<T, NbtElement> getter,
							   FieldSetter<T, NbtElement> setter,
							   String key) {
		public CompoundField<T> orElse(FieldSetter.OrElse<T, NbtElement> f) {
			return new CompoundField<>(getter, setter.orElse(f), key);
		}
	}

	static <T, R> CompoundField<T> containerField(Function<T, R> getter,
															  FieldSetter<T, R> setter,
															  Codec<R, NbtElement> codec,
															  String key) {
		return new CompoundField<>(t -> codec.toR(getter.apply(t)), (t, r) -> setter.set(t, codec.fromR(r)), key);
	}

	static <T, R> CompoundField<T> containerField(MethodHandles.Lookup l,
													 String field,
													 Codec<R, NbtElement> codec,
													 String key) {
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
		}, codec, key);
	}

	static <T, R extends Container> CompoundField<T> containerFieldInPlace(Function<T, R> getter, String key) {
		return containerFieldInPlace_(t -> Container.codec(getter.apply(t)), key);
	}

	static <T> CompoundField<T> containerFieldInPlace_(Function<T, Codec<Void, NbtElement>> getter, String key) {
		return containerField(t -> getter.apply(t).toR(null), (t, r) -> getter.apply(t).fromR(r), Codec.identity(), key);
	}

	static <T, R> CompoundField<T> containerTagField(R value,
														Codec<R, NbtElement> codec,
														String key,
														BiPredicate<R, R> equality) {
		return containerField(t -> value, (t, r) -> {
			if (!equality.test(r, value)) throw CodecException.NO_CONTEXT;
		}, codec.orElse((err, e) -> {
			throw new CodecException("Expected value of tag field " + key + " to be '" + value + "', found " + e, err);
		}), key);
	}

	static <T> ContainerCodecHelper<T> by(List<CompoundField<T>> fields) {
		return new ContainerCodecHelper<>() {
			private static <T, R> void readField(NbtCompound nbt, T container, CompoundField<T> field) throws CodecException {
				try {
					field.setter().set(container, nbt.get(field.key()));
				} catch (CodecException e) {
					throw new CodecException("field=" + field.key(), e);
				}
			}

			private static <T, R> void writeField(T container, NbtCompound nbt, CompoundField<T> field) {
				nbt.put(field.key(), field.getter().apply(container));
			}

			@Override
			public void read(NbtCompound nbt, T container) throws CodecException {
				// TODO: throwUnchecked and use forEachRemaining instead
				for (var field : fields) {
					readField(nbt, container, field);
				}
			}

			@Override
			public void write(T container, NbtCompound nbt) {
				fields.iterator().forEachRemaining(field -> writeField(container, nbt, field));
			}
		};
	}
}
