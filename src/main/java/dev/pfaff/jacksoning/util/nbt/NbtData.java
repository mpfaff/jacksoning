package dev.pfaff.jacksoning.util.nbt;

import net.minecraft.nbt.*;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface NbtData<T> {
	public byte type();

	public NbtElement toElement(T value);

	public T fromElementOrElse(@Nullable NbtElement element, Supplier<T> orElse);

	@Nullable
	public default T fromElement(NbtElement element) {
		return fromElementOrElse(element, () -> null);
	}

	public default T getOrElse(NbtCompound nbt, String key, Supplier<T> orElse) {
		return fromElementOrElse(nbt.get(key), orElse);
	}

	@Nullable
	public default T get(NbtCompound nbt, String key) {
		return getOrElse(nbt, key, () -> null);
	}

	public default T getOrElse(NbtList nbt, int index, Supplier<T> orElse) {
		return fromElementOrElse(nbt.get(index), orElse);
	}

	@Nullable
	public default T get(NbtList nbt, int index) {
		return getOrElse(nbt, index, () -> null);
	}

	static <T> NbtData<T> by(byte type,
							 Function<T, NbtElement> toElement,
							 BiFunction<NbtElement, Supplier<T>, T> fromElementOrElse) {
		return new NbtData<>() {
			@Override
			public byte type() {
				return type;
			}

			@Override
			public NbtElement toElement(T value) {
				return toElement.apply(value);
			}

			@Override
			public T fromElementOrElse(@Nullable NbtElement element, Supplier<T> orElse) {
				if (element == null || element.getType() != type) return orElse.get();
				return fromElementOrElse.apply(element, orElse);
			}
		};
	}

	static NbtData<Byte> NBT_BYTE = by(NbtElement.BYTE_TYPE, NbtByte::of, (e, orElse) -> ((NbtByte) e).byteValue());
	static NbtData<Short> NBT_SHORT = by(NbtElement.SHORT_TYPE, NbtShort::of, (e, orElse) -> ((NbtShort) e).shortValue());
	static NbtData<Integer> NBT_INT = by(NbtElement.INT_TYPE, NbtInt::of, (e, orElse) -> ((NbtInt) e).intValue());
	static NbtData<Long> NBT_LONG = by(NbtElement.LONG_TYPE, NbtLong::of, (e, orElse) -> ((NbtLong) e).longValue());
	static NbtData<Float> NBT_FLOAT = by(NbtElement.FLOAT_TYPE, NbtFloat::of, (e, orElse) -> ((NbtFloat) e).floatValue());
	static NbtData<Double> NBT_DOUBLE = by(NbtElement.DOUBLE_TYPE, NbtDouble::of, (e, orElse) -> ((NbtDouble) e).doubleValue());
	static NbtData<byte[]> NBT_BYTE_ARRAY = by(NbtElement.BYTE_ARRAY_TYPE, NbtByteArray::new, (e, orElse) -> ((NbtByteArray) e).getByteArray());
	static NbtData<String> NBT_STRING = by(NbtElement.STRING_TYPE, NbtString::of, (e, orElse) -> ((NbtString) e).asString());
	static NbtData<NbtList> NBT_LIST = by(NbtElement.LIST_TYPE, x -> x, (e, orElse) -> (NbtList) e);
	static NbtData<NbtCompound> NBT_COMPOUND = by(NbtElement.COMPOUND_TYPE, x -> x, (e, orElse) -> (NbtCompound) e);
	static NbtData<int[]> NBT_INT_ARRAY = by(NbtElement.INT_ARRAY_TYPE, NbtIntArray::new, (e, orElse) -> ((NbtIntArray) e).getIntArray());
	static NbtData<long[]> NBT_LONG_ARRAY = by(NbtElement.LONG_ARRAY_TYPE, NbtLongArray::new, (e, orElse) -> ((NbtLongArray) e).getLongArray());
}
