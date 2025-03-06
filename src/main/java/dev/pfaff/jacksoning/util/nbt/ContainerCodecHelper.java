package dev.pfaff.jacksoning.util.nbt;

import dev.pfaff.jacksoning.util.codec.Codec;
import dev.pfaff.jacksoning.util.codec.CodecException;
import net.minecraft.registry.DynamicRegistryManager;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;

import static dev.pfaff.jacksoning.util.nbt.NbtCodecs.NBT_COMPOUND;

public interface ContainerCodecHelper<T> {
	void read(NbtCompound nbt, T container) throws CodecException;

	void write(T container, NbtCompound nbt) throws CodecException;

	@FunctionalInterface
	interface FieldGetter<T, R> {
		R get(T container) throws CodecException;

		default <S> FieldGetter<T, S> then(Codec<R, S> codec) {
			return t -> codec.toR(get(t));
		}
	}

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

		default <S> FieldSetter<T, S> then(Codec<R, S> codec) {
			return (t, r) -> set(t, codec.fromR(r));
		}

		@FunctionalInterface
		public interface OrElse<T, R> {
			void set(CodecException e, T container, R value) throws CodecException;
		}
	}

	record ContainerField<T, R>(FieldGetter<T, R> getter,
								FieldSetter<T, R> setter,
								String key,
								boolean shouldSkipNulls) {
		public ContainerField(FieldGetter<T, R> getter,
							  FieldSetter<T, R> setter,
							  String key) {
			this(getter, setter, key, false);
		}

		public ContainerField<T, R> orElse(FieldSetter.OrElse<T, R> f) {
			return new ContainerField<>(getter, setter.orElse(f), key, shouldSkipNulls);
		}

		public <S> ContainerField<T, S> then(Codec<R, S> codec) {
			if (codec == Codec.IDENTITY) return (ContainerField<T, S>) this;
			var getter = this.getter;
			var setter = this.setter;
			return new ContainerField<>(getter.then(codec), setter.then(codec), key, shouldSkipNulls);
		}

		public <C, S> UnboundCompoundField<T, R, C, S> then(Function<C, Codec<R, S>> codecBinder) {
			return new UnboundCompoundField<>(this, codecBinder);
		}

		public <C, S> UnboundCompoundField<T, R, C, R> asUnbound() {
			return new UnboundCompoundField<>(this, __ -> Codec.identity());
		}

		public ContainerField<T, R> skipNulls() {
			if (shouldSkipNulls) return this;
			return new ContainerField<>(getter, setter, key, true);
		}
	}

	record UnboundCompoundField<T, R, C, S>(ContainerField<T, R> field,
											Function<C, Codec<R, S>> codecBinder) {
		public UnboundCompoundField<T, R, C, S> orElse(FieldSetter.OrElse<T, R> f) {
			return new UnboundCompoundField<>(field.orElse(f), codecBinder);
		}

		public ContainerField<T, S> bind(C context) {
			return field.then(codecBinder.apply(context));
		}
	}

	static <T, R, S> ContainerField<T, S> containerField(FieldGetter<T, R> getter,
														 FieldSetter<T, R> setter,
														 Codec<R, S> codec,
														 String key) {
		return new ContainerField<>(getter.then(codec), setter.then(codec), key);
	}

	static <T, R, S> ContainerField<T, S> containerField(MethodHandles.Lookup l,
														 String field,
														 Codec<R, S> codec,
														 String key) {
		return ContainerCodecHelper.<T, R>containerField(l, field, key).build().then(codec);
	}

	static <T, R, S> ContainerField<T, S> containerField(MethodHandles.Lookup l,
														 String field,
														 Codec<R, S> codec) {
		return containerField(l, field, codec, field);
	}

	static <T, R> ContainerFieldBuilder<T, R> containerField(MethodHandles.Lookup l, String field) {
		return containerField(l, field, field);
	}

	final class ContainerFieldBuilder<T, R> {
		private final MethodHandle getter;
		private final MethodHandle setter;
		private final boolean skipNulls;
		private final String key;

		private ContainerFieldBuilder(MethodHandle getter, MethodHandle setter, boolean skipNulls, String key) {
			this.getter = getter;
			this.setter = setter;
			this.skipNulls = skipNulls;
			this.key = key;
		}

		public ContainerField<T, R> build() {
			var MH_getter = getter;
			var MH_setter = setter;
			FieldSetter<T, R> setter = (T t, R r) -> {
				try {
					MH_setter.invoke(t, r);
				} catch (Throwable e) {
					throw new RuntimeException(e);
				}
			};
			return new ContainerField<>((T t) -> {
				try {
					return (R) MH_getter.invoke(t);
				} catch (Throwable e) {
					throw new RuntimeException(e);
				}
			}, setter, key);
		}
	}

	static <T, R> ContainerFieldBuilder<T, R> containerField(MethodHandles.Lookup l,
															 String field,
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
		return new ContainerFieldBuilder<>(getter, setter, false, key);
	}

	static <T, R extends Container> ContainerField<T, NbtElement> containerFieldInPlace(FieldGetter<T, R> getter, String key) {
		return containerFieldInPlace_(t -> Container.codec(getter.get(t)), key);
	}

	static <T, S> ContainerField<T, S> containerFieldInPlace_(FieldGetter<T, Codec<Void, S>> getter, String key) {
		return containerField(t -> getter.get(t).toR(null), (t, r) -> getter.get(t).fromR(r), Codec.identity(), key);
	}

	static <T, R, S> ContainerField<T, S> containerTagField(R value,
															Codec<R, S> codec,
															String key,
															BiPredicate<R, R> equality) {
		return containerField(t -> value, (t, r) -> {
			if (!equality.test(r, value)) throw CodecException.NO_CONTEXT;
		}, codec.orElse((err, e) -> {
			throw new CodecException("Expected value of tag field " + key + " to be '" + value + "', found " + e, err);
		}), key);
	}

	static <T> ContainerCodecHelper<T> by(List<ContainerField<T, NbtElement>> fields) {
		return new ContainerCodecHelper<>() {
			private static <T> void readField(NbtCompound nbt, T container, ContainerField<T, NbtElement> field) throws CodecException {
				try {
					field.setter().set(container, nbt.get(field.key()));
				} catch (CodecException e) {
					throw new CodecException("field=" + field.key(), e);
				}
			}

			private static <T> void writeField(T container, NbtCompound nbt, ContainerField<T, NbtElement> field) throws CodecException {
				try {
					NbtElement value = field.getter.get(container);
					if (value == null) {
						if (!field.shouldSkipNulls) throw new CodecException("null value returned from field getter");
						return;
					}
					nbt.put(field.key(), value);
				} catch (CodecException e) {
					throw new CodecException("field=" + field.key(), e);
				}
			}

			@Override
			public void read(NbtCompound nbt, T container) throws CodecException {
				for (var field : fields) {
					readField(nbt, container, field);
				}
			}

			@Override
			public void write(T container, NbtCompound nbt) throws CodecException {
				for (var field : fields) {
					writeField(container, nbt, field);
				}
			}
		};
	}

	static <T> ContainerCodecHelper<T> componentBy(String name, List<ContainerField<T, NbtElement>> fields) {
		var codec = by(fields);
		return new ContainerCodecHelper<>() {

			@Override
			public void read(NbtCompound nbt, T container) throws CodecException {
				try {
					var component = nbt.get(name);
					if (component == null) {
						codec.read(NbtCompound.empty(), container);
					} else {
						codec.read(NBT_COMPOUND.fromR(component), container);
					}
				} catch (CodecException e) {
					throw new CodecException("field=" + name, e);
				}
			}

			@Override
			public void write(T container, NbtCompound nbt) throws CodecException {
				try {
					var component = nbt.getOrPutCompound(name);
					codec.write(container, component);
				} catch (CodecException e) {
					throw new CodecException("field=" + name, e);
				}
			}
		};
	}

	static <T> Function<DynamicRegistryManager, ContainerCodecHelper<T>> byUnbound(List<UnboundCompoundField<T, ?, DynamicRegistryManager, NbtElement>> fields) {
		return registryManager -> by(fields.stream().map(field -> {
			return field.bind(registryManager);
		}).toList());
	}

	static <T> Function<DynamicRegistryManager, ContainerCodecHelper<T>> componentByUnbound(String name, List<UnboundCompoundField<T, ?, DynamicRegistryManager, NbtElement>> fields) {
		return registryManager -> componentBy(name, fields.stream().map(field -> {
			return field.bind(registryManager);
		}).toList());
	}
}
