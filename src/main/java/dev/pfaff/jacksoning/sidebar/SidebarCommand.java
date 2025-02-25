package dev.pfaff.jacksoning.sidebar;

import com.google.common.base.Preconditions;
import dev.pfaff.jacksoning.util.NetworkUtil;
import dev.pfaff.jacksoning.util.StringOrText;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

import java.util.List;

import static dev.pfaff.jacksoning.util.NetworkUtil.readString;

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
			case OPCODE_SET_LINE_STRING -> {
				int index = buf.readUnsignedByte();
				byte flags = buf.readByte();
				yield new SetLine(index, new StringOrText(readString(buf), null), flags);
			}
			case OPCODE_SET_LINE_TEXT -> {
				int index = buf.readUnsignedByte();
				byte flags = buf.readByte();
				yield new SetLine(index, new StringOrText(null, TextCodecs.PACKET_CODEC.decode(buf)), flags);
			}
			default -> throw new DecoderException("Unrecognized sidebar command: " + tag);
		};
	}

	static Truncate truncate(int length) {
		Preconditions.checkPositionIndex(length, 255, "Line count out of bounds");
		return TRUNCATES.get(length);
	}

	static SetLine setLine(int index, StringOrText text, Alignment alignment) {
		return new SetLine(index, text, (byte) alignment.ordinal());
	}

	static SetLine setLine(int index, String string, Alignment alignment) {
		return setLine(index, StringOrText.of(string), alignment);
	}

	static SetLine setLine(int index, Text text, Alignment alignment) {
		return setLine(index, StringOrText.of(text), alignment);
	}

	static SetLine setLine(int index, StringOrText text) {
		return setLine(index, text, Alignment.Start);
	}

	static SetLine setLine(int index, String string) {
		return setLine(index, StringOrText.of(string));
	}

	static SetLine setLine(int index, Text text) {
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

	public record SetLine(int index, StringOrText text, byte flags) implements SidebarCommand {
		public SetLine {
			Preconditions.checkElementIndex(index, 254, "Line index out of bounds");
		}

		public Alignment alignment() {
			return Alignment.fromByte(flags);
		}

		@Override
		public int opcode() {
			return text.string() != null ? OPCODE_SET_LINE_STRING : OPCODE_SET_LINE_TEXT;
		}

		@Override
		public void writePacket(PacketByteBuf buf) {
			SidebarCommand.super.writePacket(buf);
			buf.writeByte(index);
			buf.writeByte(flags);
			if (text.string() != null) {
				NetworkUtil.writeStringWithTruncation(buf, text.string());
			} else {
				TextCodecs.PACKET_CODEC.encode(buf, text.text());
			}
		}
	}
}
