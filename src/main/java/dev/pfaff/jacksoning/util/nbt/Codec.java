package dev.pfaff.jacksoning.util.nbt;

import net.minecraft.nbt.*;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Codec<T> {
	public byte type();

	public NbtElement toElement(T value);

	public T fromElement(@Nullable NbtElement element, Function<NbtElement, T> orElse);

	public default T fromElement(@Nullable NbtElement element, Supplier<T> orElse) {
		return fromElement(element, e -> orElse.get());
	}

	@Nullable
	public default T fromElementOrNull(NbtElement element) {
		return fromElement(element, () -> null);
	}

	public default void put(NbtCompound nbt, String key, T value) {
		nbt.put(key, toElement(value));
	}

	public default T get(NbtCompound nbt, String key, Function<NbtElement, T> orElse) {
		return fromElement(nbt.get(key), orElse);
	}

	public default T get(NbtCompound nbt, String key, Supplier<T> orElse) {
		return fromElement(nbt.get(key), orElse);
	}

	@Nullable
	public default T getOrNull(NbtCompound nbt, String key) {
		return get(nbt, key, () -> null);
	}

	public default void add(NbtList nbt, T value) {
		nbt.add(toElement(value));
	}

	public default void insert(NbtList nbt, int index, T value) {
		nbt.add(index, toElement(value));
	}

	public default void set(NbtList nbt, int index, T value) {
		nbt.set(index, toElement(value));
	}

	public default T get(NbtList nbt, int index, Function<NbtElement, T> orElse) {
		return fromElement(nbt.get(index), orElse);
	}

	public default T get(NbtList nbt, int index, Supplier<T> orElse) {
		return fromElement(nbt.get(index), orElse);
	}

	@Nullable
	public default T getOrNull(NbtList nbt, int index) {
		return get(nbt, index, () -> null);
	}

	static <T> Codec<T> by(byte type,
						   Function<T, NbtElement> toElement,
						   BiFunction<NbtElement, Function<NbtElement, T>, T> fromElementOrElse) {
		return new Codec<>() {
			@Override
			public byte type() {
				return type;
			}

			@Override
			public NbtElement toElement(T value) {
				return toElement.apply(value);
			}

			@Override
			public T fromElement(@Nullable NbtElement element, Function<NbtElement, T> orElse) {
				if (element == null || element.getType() != type) return orElse.apply(element);
				return fromElementOrElse.apply(element, orElse);
			}
		};
	}

	//static <T extends Container> Codec<T> container() {
	//	return new ContainerCodec<>();
	//}
	//
	//final class ContainerCodec<T extends Container> implements Codec<T> {
	//
	//
	//	@Override
	//	public byte type() {
	//		return NBT_COMPOUND.type();
	//	}
	//
	//	@Override
	//	public NbtCompound toElement(T value) {
	//		var nbt = new NbtCompound();
	//		value.write(nbt);
	//		return nbt;
	//	}
	//
	//	@Override
	//	public T fromElement(@Nullable NbtElement element, Function<NbtElement, T> orElse) {
	//		var nbt = NBT_COMPOUND.fromElement(element, e -> toElement(orElse.apply(e)));
	//		return ;
	//	}
	//}

	static Codec<Boolean> NBT_BOOL = by(NbtElement.BYTE_TYPE,
										NbtByte::of,
										(e, orElse) -> ((NbtByte) e).byteValue() != 0);
	static Codec<Byte> NBT_BYTE = by(NbtElement.BYTE_TYPE, NbtByte::of, (e, orElse) -> ((NbtByte) e).byteValue());
	static Codec<Short> NBT_SHORT = by(NbtElement.SHORT_TYPE, NbtShort::of, (e, orElse) -> ((NbtShort) e).shortValue());
	static Codec<Integer> NBT_INT = by(NbtElement.INT_TYPE, NbtInt::of, (e, orElse) -> ((NbtInt) e).intValue());
	static Codec<Long> NBT_LONG = by(NbtElement.LONG_TYPE, NbtLong::of, (e, orElse) -> ((NbtLong) e).longValue());
	static Codec<Float> NBT_FLOAT = by(NbtElement.FLOAT_TYPE, NbtFloat::of, (e, orElse) -> ((NbtFloat) e).floatValue());
	static Codec<Double> NBT_DOUBLE = by(NbtElement.DOUBLE_TYPE,
										 NbtDouble::of,
										 (e, orElse) -> ((NbtDouble) e).doubleValue());
	static Codec<byte[]> NBT_BYTE_ARRAY = by(NbtElement.BYTE_ARRAY_TYPE,
											 NbtByteArray::new,
											 (e, orElse) -> ((NbtByteArray) e).getByteArray());
	static Codec<String> NBT_STRING = by(NbtElement.STRING_TYPE,
										 NbtString::of,
										 (e, orElse) -> ((NbtString) e).asString());
	static Codec<NbtList> NBT_LIST = by(NbtElement.LIST_TYPE, x -> x, (e, orElse) -> (NbtList) e);
	static Codec<NbtCompound> NBT_COMPOUND = by(NbtElement.COMPOUND_TYPE, x -> x, (e, orElse) -> (NbtCompound) e);
	static Codec<int[]> NBT_INT_ARRAY = by(NbtElement.INT_ARRAY_TYPE,
										   NbtIntArray::new,
										   (e, orElse) -> ((NbtIntArray) e).getIntArray());
	static Codec<long[]> NBT_LONG_ARRAY = by(NbtElement.LONG_ARRAY_TYPE,
											 NbtLongArray::new,
											 (e, orElse) -> ((NbtLongArray) e).getLongArray());
	static Codec<List<BlockPos>> NBT_FLAT_BLOCK_POS_LIST = by(NbtElement.INT_ARRAY_TYPE, l -> {
		var flat = new int[l.size() * 3];
		for (int i = 0; i < l.size(); i++) {
			flat[i * 3] = l.get(i).getX();
			flat[i * 3 + 1] = l.get(i).getY();
			flat[i * 3 + 2] = l.get(i).getZ();
		}
		return new NbtIntArray(flat);
	}, (e, orElse) -> {
		var flat = ((NbtIntArray) e).getIntArray();
		var len = flat.length / 3;
		if (flat.length % 3 != 0) return orElse.apply(e);
		var a = new BlockPos[len];
		for (int i = 0; i < a.length; i++) {
			a[i] = new BlockPos(flat[i * 3], flat[i * 3 + 1], flat[i * 3 + 2]);
		}
		return List.of(a);
	});
}
