package dev.pfaff.jacksoning.client;

import dev.pfaff.jacksoning.packet.UpdateUIPacket;
import dev.pfaff.jacksoning.sidebar.Alignment;
import dev.pfaff.jacksoning.sidebar.SidebarCommand;
import dev.pfaff.jacksoning.util.OpenArrayList;
import dev.pfaff.jacksoning.util.StringOrText;
import dev.pfaff.jacksoning.util.gui.GuiGlobals;
import io.netty.util.internal.EmptyArrays;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import org.slf4j.event.Level;

import java.util.Arrays;

import static dev.pfaff.jacksoning.util.gui.GuiGlobals.mcWindow;

public final class ClientSidebar {
	private static final OpenArrayList<StringOrText> lines = OpenArrayList.wrap(StringOrText.EMPTY_ARRAY);
	private static Alignment[] alignments = Alignment.EMPTY_ARRAY;
	private static int[] widthBuffer = EmptyArrays.EMPTY_INTS;
//	private static final BitSet dirty = new BitSet();

	private static final TextRenderer textRenderer = GuiGlobals.textRenderer;

	private static final EdgeInsets padding = EdgeInsets.symmetrical(4, 2);
	private static final EdgeInsets margin = EdgeInsets.symmetrical(4, 2);

	public static void render(DrawContext context, RenderTickCounter tickCounter) {
		var lines = ClientSidebar.lines;
		var l = lines.size();
		var linesArray = lines.a();
		var alignments = ClientSidebar.alignments;
		var widthBuffer = ClientSidebar.widthBuffer;
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
		int endX = mcWindow.getScaledWidth() - margin.right();
		int startX = endX - maxWidth - padding.horizontal();
		int y = mcWindow.getScaledHeight() / 2 - lines.size() * fontHeight / 2;
		context.fill(startX,
					 y - padding.top(),
					 endX,
					 y + lines.size() * fontHeight + padding.bottom(),
					 GuiGlobals.GENERIC_HUD_BACKGROUND);
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
					case Middle -> x += maxWidth / 2 - widthBuffer[i] / 2;
					case End -> x += maxWidth - widthBuffer[i];
				}
			}
			if (line.string() != null) {
				context.drawText(textRenderer, line.string(), x, y, 0xE0E0E0, false);
			} else {
				context.drawText(textRenderer, line.text(), x, y, 0xE0E0E0, false);
			}
			y += fontHeight;
		}
	}

	public static void handleUpdate(UpdateUIPacket packet) {
		final var lines = ClientSidebar.lines;
		var len = lines.size();
		var linesArray = lines.a();
		for (var command : packet.commands()) {
			JacksoningClient.LOGGER.log(Level.DEBUG, () -> "Received sidebar command: " + command);
			switch (command) {
				case SidebarCommand.Truncate cmd -> {
					var newLinesArray = lines.ensureCapacityAndReturnArray(len = cmd.length());
					//noinspection ArrayEquality
					if (newLinesArray != linesArray) {
//						dirty.set(newLinesArray.length);
						linesArray = newLinesArray;
						alignments = Arrays.copyOf(alignments, linesArray.length);
						widthBuffer = new int[linesArray.length];
					}
				}
				case SidebarCommand.SetLine cmd -> {
					linesArray[cmd.index()] = cmd.text();
					alignments[cmd.index()] = cmd.alignment() != Alignment.Start ? cmd.alignment() : null;
//					dirty.set(cmd.index());
				}
			}
		}
		lines.setSizeAssumeCapacityAndInitialized(len);
	}
}
