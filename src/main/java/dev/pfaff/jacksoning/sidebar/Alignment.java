package dev.pfaff.jacksoning.sidebar;

public enum Alignment {
	Left,
	Center,
	Right;

	public static final Alignment[] EMPTY_ARRAY = new Alignment[0];

	public static Alignment fromByte(byte b) {
		b &= 0b11;
		if (b == Center.ordinal()) return Center;
		if (b == Right.ordinal()) return Right;
		return Left;
	}
}
