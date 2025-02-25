package dev.pfaff.jacksoning.util.gui;

import dev.pfaff.jacksoning.util.LogDestupify;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.Window;
import org.slf4j.LoggerFactory;

/**
 * Holds {@code static final} references so that the JVM can optimize better, and makes usages less verbose.
 */
public final class GuiGlobals {
	public static final LogDestupify LOGGER = new LogDestupify(LoggerFactory.getLogger("AltarGUI"));

	public static final int GENERIC_HUD_BACKGROUND = 0x90_505050;

	public static final MinecraftClient client = MinecraftClient.getInstance();
	public static final Window mcWindow = client.getWindow();
	public static final TextRenderer textRenderer = client.textRenderer;
}
