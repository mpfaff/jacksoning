package dev.pfaff.jacksoning;

import com.google.common.base.Preconditions;
import dev.pfaff.jacksoning.util.StringOrText;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.util.List;

import static dev.pfaff.jacksoning.util.NetworkUtil.readTextReprString;
import static dev.pfaff.jacksoning.util.NetworkUtil.readTextReprText;
import static dev.pfaff.jacksoning.util.NetworkUtil.writeTextUntagged;

public sealed interface SidebarCommand {
	static final int OPCODE_TRUNCATE = 0;
	static final int OPCODE_SET_LINE_STRING = 1;
	static final int OPCODE_SET_LINE_TEXT = 2;
	static final List<Truncate> TRUNCATES = initializeTruncates();

	static List<Truncate> initializeTruncates() {
		Truncate[] truncates = new Truncate[256];
		for (int i = 0; i < truncates.length; i++) {
			truncates[i] = new Truncate(i);
		}
		return List.of(truncates);
	}

	int opcode();

	@MustBeInvokedByOverriders
	default void writePacket(PacketByteBuf buf) {
		buf.writeByte(opcode());
	}

	static SidebarCommand fromPacket(PacketByteBuf buf) {
		byte tag = buf.readByte();
		return switch (tag) {
			case OPCODE_TRUNCATE -> new Truncate(buf.readUnsignedByte());
			case OPCODE_SET_LINE_STRING -> setLine(buf.readUnsignedByte(),
												   new StringOrText(readTextReprString(buf), null));
			case OPCODE_SET_LINE_TEXT -> setLine(buf.readUnsignedByte(), new StringOrText(null, readTextReprText(buf)));
			default -> throw new DecoderException("Unrecognized sidebar command: " + tag);
		};
	}

	static SidebarCommand truncate(int length) {
		Preconditions.checkPositionIndex(length, 255, "Line count out of bounds");
		return TRUNCATES.get(length);
	}

	static SidebarCommand setLine(int index, String string) {
		return setLine(index, StringOrText.of(string));
	}

	static SidebarCommand setLine(int index, StringOrText text) {
		return new SetLine(index, text);
	}

	static SidebarCommand setLine(int index, Text text) {
		return setLine(index, StringOrText.of(text));
	}

	public record Truncate(int length) implements SidebarCommand {
		public Truncate {
			Preconditions.checkPositionIndex(length, 255, "Line count out of bounds");
		}

		@Override
		public int opcode() {
			return OPCODE_TRUNCATE;
		}

		@Override
		public void writePacket(PacketByteBuf buf) {
			SidebarCommand.super.writePacket(buf);
			buf.writeByte(length);
		}
	}

	public record SetLine(int index, StringOrText text) implements SidebarCommand {
		public SetLine {
			Preconditions.checkElementIndex(index, 254, "Line index out of bounds");
		}

		@Override
		public int opcode() {
			return text.string() != null ? OPCODE_SET_LINE_STRING : OPCODE_SET_LINE_TEXT;
		}

		@Override
		public void writePacket(PacketByteBuf buf) {
			SidebarCommand.super.writePacket(buf);
			buf.writeByte(index);
			if (text.string() != null) {
				writeTextUntagged(buf, text.string());
			} else {
				writeTextUntagged(buf, text.text());
			}
		}
	}
}
