package dev.pfaff.jacksoning.sidebar;

public enum Alignment {
	Start,
	Middle,
	End;

	public static final Alignment[] EMPTY_ARRAY = new Alignment[0];

	public static Alignment fromByte(byte b) {
		b &= 0b11;
		if (b == Middle.ordinal()) return Middle;
		if (b == End.ordinal()) return End;
		return Start;
	}
}
