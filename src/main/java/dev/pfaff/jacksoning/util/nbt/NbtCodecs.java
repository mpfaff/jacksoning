package dev.pfaff.jacksoning.util.nbt;

import net.minecraft.nbt.*;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import static dev.pfaff.jacksoning.util.nbt.NbtElement.TYPE_NAMES;
import static net.minecraft.nbt.NbtElement.*;

public final class NbtCodecs {
	public static <T> Codec<T, NbtElement> by(Function<T, @NotNull NbtElement> toElement,
											  FromR<T, @NotNull NbtElement> fromElement) {
		var nullMsg = "Expected an NBT value, found null";
		return new Codec<>() {
			@Override
			@NotNull
			public NbtElement toR(T value) {
				return toElement.apply(value);
			}

			@Override
			public T fromR(@Nullable NbtElement element) throws CodecException {
				if (element == null) throw new CodecException(nullMsg);
				return fromElement.fromR(element);
			}
		};
	}

	public static <T> Codec<T, NbtElement> by(byte type,
											  Function<T, NbtElement> toElement,
											  FromR<T, NbtElement> fromElement) {
		return by(toElement, element -> {
			if (element.type() != type) {
				throw new CodecException("Expected an NBT value of type " + TYPE_NAMES.get(type) + ", found " + TYPE_NAMES.get(
					element.type()));
			}
			return fromElement.fromR(element);
		});
	}

	public static <T> Codec<T, NbtElement> by(List<Byte> types,
											  Function<T, NbtElement> toElement,
											  FromR<T, NbtElement> fromElement) {
		var typeNames = IntStream.range(0, TYPE_NAMES.size())
								 .filter(i -> types.contains((byte) i))
								 .mapToObj(TYPE_NAMES::get)
								 .toList();
		return by(toElement, element -> {
			if (!types.contains(element.type())) {
				throw new CodecException("Expected one of " + typeNames + ", found " + TYPE_NAMES.get(element.type()));
			}
			return fromElement.fromR(element);
		});
	}

	public static Codec<Boolean, NbtElement> NBT_BOOL = by(BYTE_TYPE,
														   t -> MinecraftNbtWrapper.of(NbtByte.of(t)),
														   e -> e.asByte() != 0);
	public static Codec<Byte, NbtElement> NBT_BYTE = by(BYTE_TYPE,
														t -> MinecraftNbtWrapper.of(NbtByte.of(t)),
														NbtElement::asByte);
	public static Codec<Short, NbtElement> NBT_SHORT = by(SHORT_TYPE,
														  t -> MinecraftNbtWrapper.of(NbtShort.of(t)),
														  NbtElement::asShort);
	public static Codec<Integer, NbtElement> NBT_INT = by(INT_TYPE,
														  t -> MinecraftNbtWrapper.of(NbtInt.of(t)),
														  NbtElement::asInt);
	public static Codec<Long, NbtElement> NBT_LONG = by(LONG_TYPE,
														t -> MinecraftNbtWrapper.of(NbtLong.of(t)),
														NbtElement::asLong);
	public static Codec<Float, NbtElement> NBT_FLOAT = by(FLOAT_TYPE,
														  t -> MinecraftNbtWrapper.of(NbtFloat.of(t)),
														  NbtElement::asFloat);
	public static Codec<Double, NbtElement> NBT_DOUBLE = by(DOUBLE_TYPE,
															t -> MinecraftNbtWrapper.of(NbtDouble.of(t)),
															NbtElement::asDouble);
	public static Codec<byte[], NbtElement> NBT_BYTE_ARRAY = by(BYTE_ARRAY_TYPE,
																t -> MinecraftNbtWrapper.of(new NbtByteArray(t)),
																NbtElement::asByteArray);
	public static Codec<String, NbtElement> NBT_STRING = by(STRING_TYPE,
															t -> MinecraftNbtWrapper.of(NbtString.of(t)),
															NbtElement::asString);
	public static Codec<NbtList, NbtElement> NBT_LIST = by(LIST_TYPE, x -> x, NbtElement::asList);
	public static Codec<NbtCompound, NbtElement> NBT_COMPOUND = by(COMPOUND_TYPE, x -> x, NbtElement::asCompound);
	public static Codec<int[], NbtElement> NBT_INT_ARRAY = by(INT_ARRAY_TYPE,
															  t -> MinecraftNbtWrapper.of(new NbtIntArray(t)),
															  NbtElement::asIntArray);
	public static Codec<long[], NbtElement> NBT_LONG_ARRAY = by(LONG_ARRAY_TYPE,
																t -> MinecraftNbtWrapper.of(new NbtLongArray(t)),
																NbtElement::asLongArray);
	public static Codec<List<BlockPos>, NbtElement> NBT_FLAT_BLOCK_POS_LIST = NBT_INT_ARRAY.then(Codec.by(l -> {
		var flat = new int[l.size() * 3];
		for (int i = 0; i < l.size(); i++) {
			flat[i * 3] = l.get(i).getX();
			flat[i * 3 + 1] = l.get(i).getY();
			flat[i * 3 + 2] = l.get(i).getZ();
		}
		return flat;
	}, flat -> {
		var len = flat.length / 3;
		if (flat.length % 3 != 0) {
			throw new CodecException("Expected an int array with a multiple of 3 elements, found one with " + flat.length + " elements");
		}
		var a = new BlockPos[len];
		for (int i = 0; i < a.length; i++) {
			a[i] = new BlockPos(flat[i * 3], flat[i * 3 + 1], flat[i * 3 + 2]);
		}
		return List.of(a);
	}));
}
