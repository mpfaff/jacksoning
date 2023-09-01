package dev.pfaff.jacksoning.util.nbt;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record NbtTypeSet(short bits) {
	public static final NbtTypeSet NONE = new NbtTypeSet((short) 0);
	public static final NbtTypeSet ALL = new NbtTypeSet((short) NbtType.VALUES.stream()
																			  .mapToInt(NbtType::id)
																			  .reduce(0,
																					  (bits, type) -> bits | bitsFromId(
																						  (byte) type)));
	public static final List<NbtTypeSet> SINGLES = NbtType.VALUES.stream()
																 .map(type -> new NbtTypeSet(bitsFromId(type.id())))
																 .toList();

	private static short bitsFromId(byte id) {
		return (short) (1 << id);
	}

	private static NbtTypeSet ofBits(short bits) {
		if (bits == 0) return NONE;
		// mask out any extra bits
		bits &= ALL.bits;
		if (bits == ALL.bits()) return NONE;
		if (Integer.bitCount(bits) == 1) return SINGLES.get(Integer.numberOfTrailingZeros(bits));
		return new NbtTypeSet(bits);
	}

	public static NbtTypeSet of(byte type) {
		return ofBits(bitsFromId(type));
	}

	public static NbtTypeSet ofRaw(List<Byte> types) {
		short bits = 0;
		for (byte type : types) {
			bits |= bitsFromId(type);
		}
		return ofBits(bits);
	}

	public static NbtTypeSet of(NbtType type) {
		return SINGLES.get(type.id());
	}

	public static NbtTypeSet of(List<NbtType> types) {
		short bits = 0;
		for (NbtType type : types) {
			bits |= type.id();
		}
		return ofBits(bits);
	}

	public boolean contains(byte type) {
		return (this.bits & bitsFromId(type)) != 0;
	}

	public boolean contains(NbtType type) {
		return contains(type.id());
	}

	/**
	 * The elementwise NOT of the set.
	 */
	public NbtTypeSet complement() {
		return ofBits((short) ~bits);
	}

	/**
	 * The elementwise OR of the sets.
	 */
	public NbtTypeSet union(NbtTypeSet other) {
		return ofBits((short) (bits | other.bits));
	}

	/**
	 * The elementwise AND of the sets.
	 */
	public NbtTypeSet intersection(NbtTypeSet other) {
		return ofBits((short) (bits & other.bits));
	}

	public void forEach(Consumer<NbtType> consumer) {
		for (NbtType t : NbtType.VALUES) {
			if (contains(t)) consumer.accept(t);
		}
	}

	public Stream<NbtType> stream() {
		return NbtType.VALUES.stream().filter(this::contains);
	}

	public IntStream idStream() {
		return IntStream.range(0, NbtType.VALUES.size()).filter(i -> this.contains((byte) i));
	}
}
