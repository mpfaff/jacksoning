package dev.pfaff.jacksoning.client;

import dev.pfaff.jacksoning.sidebar.Alignment;
import dev.pfaff.jacksoning.sidebar.SidebarCommand;
import dev.pfaff.jacksoning.util.OpenArrayList;
import dev.pfaff.jacksoning.util.StringOrText;
import io.netty.util.internal.EmptyArrays;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import org.slf4j.event.Level;

import java.util.Arrays;

public final class ClientSidebar {
	private static final OpenArrayList<StringOrText> lines = OpenArrayList.wrap(StringOrText.EMPTY_ARRAY);
	private static Alignment[] alignments = Alignment.EMPTY_ARRAY;
	private static int[] widthBuffer = EmptyArrays.EMPTY_INTS;

	private static final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

	private static final EdgeInsets padding = EdgeInsets.symmetrical(4, 2);
	private static final EdgeInsets margin = EdgeInsets.symmetrical(4, 2);

	public static void render(MatrixStack matrixStack, float tickDelta) {
		var lines = ClientSidebar.lines;
		var l = lines.size();
		var linesArray = lines.a();
		var alignments = ClientSidebar.alignments;
		var widthBuffer = ClientSidebar.widthBuffer;
		var client = MinecraftClient.getInstance();
		int maxWidth = 0;
		for (var i = 0; i < l; i++) {
			var line = linesArray[i];
			if (line == null) continue;
			var width = line.string() != null
						? textRenderer.getWidth(line.string())
						: textRenderer.getWidth(line.text());
			widthBuffer[i] = width;
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
		int paddedX = startX + padding.left();
		for (var i = 0; i < l; i++) {
			var line = linesArray[i];
			if (line == null) continue;
			int x = paddedX;
			var align = alignments[i];
			if (align != null) {
				// fun to make these little branchless things, but it's probably completely unnecessary.
				//// no negative numbers, no problem.
				//assert widthBuffer[i] >= 0;
				//int shift = switch (align) {
				//	case Left -> 31; // can't use 32, that wraps around to 0. 31 works as long as the sign bit is unset.
				//	case Center -> 1; // div by 2
				//	case Right -> 0; // no change
				//};
				//x += maxWidth >>> shift - widthBuffer[i] >>> shift;
				switch (align) {
					case Center -> x += maxWidth / 2 - widthBuffer[i] / 2;
					case Right -> x += maxWidth - widthBuffer[i];
				}
			}
			if (line.string() != null) {
				textRenderer.draw(matrixStack, line.string(), x, y, 0xE0E0E0);
			} else {
				textRenderer.draw(matrixStack, line.text(), x, y, 0xE0E0E0);
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
			JacksoningClient.LOGGER.log(Level.DEBUG, () -> "Received sidebar command: " + command);
			switch (command) {
				case SidebarCommand.Truncate cmd -> {
					var newLinesArray = lines.ensureCapacityAndReturnArray(len = cmd.length());
					//noinspection ArrayEquality
					if (newLinesArray != linesArray) {
						linesArray = newLinesArray;
						alignments = Arrays.copyOf(alignments, linesArray.length);
						widthBuffer = new int[linesArray.length];
					}
				}
				case SidebarCommand.SetLine cmd -> {
					linesArray[cmd.index()] = cmd.text();
					alignments[cmd.index()] = cmd.alignment() != Alignment.Left ? cmd.alignment() : null;
				}
			}
		}
		lines.setSizeAssumeCapacityAndInitialized(len);
	}
}
