package dev.pfaff.jacksoning;

import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class Config {
	public static int ECONOMY_BOOST_PER_OUTPUT = 2;

	public static int JACKSON_ZONE_RADIUS = 50;

	/**
	 * Reduces cooldowns, ignores some checks.
	 */
	public static boolean DEV_MODE = true;

	public static boolean ALLOW_INCOMPLETE_CAST = DEV_MODE;

	/**
	 * Every 4 minutes:
	 * 20 ticks per second, 60 per minute, 4 per interval
	 */
	public static final int GROOVE_INTERVAL = 20 * 60 * 4;

	public static final int RESPAWN_COOLDOWN = DEV_MODE ? 20 * 5 : 20 * 30 * 5;

	public static int JACKSON_BASE_HEALTH_BOOST = 20 * 3;

	public static void throwIncompleteCastError(MinecraftServer server, String message) {
		if (ALLOW_INCOMPLETE_CAST) {
			server.getPlayerManager().getPlayerList().forEach(p -> p.sendMessage(Text.literal(message)
																					 .styled(s -> s.withColor(
																						 Formatting.YELLOW))));
		} else {
			throw new IllegalStateException(message);
		}
	}
}
