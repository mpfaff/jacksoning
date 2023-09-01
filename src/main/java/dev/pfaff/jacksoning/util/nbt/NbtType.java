package dev.pfaff.jacksoning.util.nbt;

import java.util.List;

public enum NbtType {
	END(0),
	BYTE(1),
	SHORT(2),
	INT(3),
	LONG(4),
	FLOAT(5),
	DOUBLE(6),
	BYTE_ARRAY(7),
	STRING(8),
	LIST(9),
	COMPOUND(10),
	INT_ARRAY(11),
	LONG_ARRAY(12);

	private NbtType(int i) {
		if (i != ordinal()) throw new AssertionError();
	}

	public final byte id() {
		return (byte) ordinal();
	}

	public static final List<NbtType> VALUES = List.of(values());
	public static final List<String> NAMES = VALUES.stream().map(NbtType::name).toList();

	public static boolean isValid(byte type) {
		return type >= 0 && type < VALUES.size();
	}

	public static String nameOf(byte type) {
		return isValid(type) ? NAMES.get(type) : "INVALID[" + type + "]";
	}
}
