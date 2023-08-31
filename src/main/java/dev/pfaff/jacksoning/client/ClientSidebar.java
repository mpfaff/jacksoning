package dev.pfaff.jacksoning.client;

import dev.pfaff.jacksoning.util.OpenArrayList;
import dev.pfaff.jacksoning.SidebarCommand;
import dev.pfaff.jacksoning.util.StringOrText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;

public final class ClientSidebar {
	private static final OpenArrayList<StringOrText> lines = OpenArrayList.wrap(StringOrText.EMPTY_ARRAY);

	private static final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

	private static final EdgeInsets padding = EdgeInsets.symmetrical(4, 2);
	private static final EdgeInsets margin = EdgeInsets.symmetrical(4, 2);

	public static void render(MatrixStack matrixStack, float tickDelta) {
		var lines = ClientSidebar.lines;
		var l = lines.size();
		var linesArray = lines.a();
		var client = MinecraftClient.getInstance();
		int maxWidth = 0;
		for (var i = 0; i < l; i++) {
			var line = linesArray[i];
			if (line == null) continue;
			var width = line.string() != null
						? textRenderer.getWidth(line.string())
						: textRenderer.getWidth(line.text());
			maxWidth = Math.max(maxWidth, width);
		}
		int fontHeight = textRenderer.fontHeight;
		int endX = client.getWindow().getScaledWidth() - margin.right();
		int startX = endX - maxWidth - padding.horizontal();
		int y = client.getWindow().getScaledHeight() / 2 - lines.size() * fontHeight / 2;
		DrawableHelper.fill(matrixStack,
							startX,
							y - padding.top(),
							endX,
							y + lines.size() * fontHeight + padding.bottom(),
							0x90_505050);
		for (var i = 0; i < l; i++) {
			var line = linesArray[i];
			if (line == null) continue;
			if (line.string() != null) {
				textRenderer.draw(matrixStack, line.string(), startX + padding.left(), y, 0xE0E0E0);
			} else {
				textRenderer.draw(matrixStack, line.text(), startX + padding.left(), y, 0xE0E0E0);
			}
			y += fontHeight;
		}
	}

	public static void handleUpdate(PacketByteBuf buf) {
		final var lines = ClientSidebar.lines;
		var len = lines.size();
		var linesArray = lines.a();
		while (buf.isReadable()) {
			var command = SidebarCommand.fromPacket(buf);
			JacksoningClient.LOGGER.debug("Received sidebar command: {}", command);
			switch (command) {
				case SidebarCommand.Truncate cmd -> linesArray = lines.ensureCapacityAndReturnArray(len = cmd.length());
				case SidebarCommand.SetLine cmd -> linesArray[cmd.index()] = cmd.text();
			}
		}
		lines.setSizeAssumeCapacityAndInitialized(len);
	}
}
