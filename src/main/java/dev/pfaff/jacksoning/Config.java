package dev.pfaff.jacksoning;

public final class Config {
	public static int economyBoostPerOutput() {
		return 2;
	}

	public static int jacksonZoneRadius() {
		return 50;
	}

	/**
	 * Every 4 minutes: 20 ticks per second, 60 per minute, 4 per interval
	 */
	public static int grooveInterval() {
		return 20 * 60 * 4;
	}

	public static int jacksonBaseHealthBoost() {
		return 20 * 3;
	}
}
