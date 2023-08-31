package dev.pfaff.jacksoning;

import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class Config {
	public static int economyBoostPerOutput() {
		return 2;
	}

	public static int jacksonZoneRadius() {
		return 50;
	}

	private static boolean enableDevMode = false;

	/**
	 * Reduces cooldowns, ignores some checks.
	 */
	public static boolean devMode() {
		return enableDevMode;
	}

	public static void devMode(boolean enable) {
		enableDevMode = enable;
	}

	public static boolean allowIncompleteCast() {
		return devMode();
	}

	/**
	 * Every 4 minutes: 20 ticks per second, 60 per minute, 4 per interval
	 */
	public static int grooveInterval() {
		return 20 * 60 * 4;
	}

	public static int respawnCooldown() {
		return devMode() ? 20 * 5 : 20 * 30 * 5;
	}

	public static int jacksonSpawnDelay() {
		return devMode() ? 20 * 5 : 20 * 60 * 5;
	}

	public static int jacksonBaseHealthBoost() {
		return 20 * 3;
	}

	public static void throwIncompleteCastError(MinecraftServer server, String message) {
		if (allowIncompleteCast()) {
			server.getPlayerManager().getPlayerList().forEach(p -> p.sendMessage(Text.literal(message)
																					 .styled(s -> s.withColor(Formatting.YELLOW))));
		} else {
			throw new IllegalStateException(message);
		}
	}
}
