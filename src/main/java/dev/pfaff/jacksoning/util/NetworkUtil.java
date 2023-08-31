package dev.pfaff.jacksoning.util;

import dev.pfaff.jacksoning.Jacksoning;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

import java.nio.charset.StandardCharsets;

/**
 * Some networking utilities.
 * <p>
 * The string utilities optimize by taking advantage of some assumptions, like that the strings will only ever contain
 * ASCII characters.
 */
public final class NetworkUtil {
	public static final int TEXT_REPR_STRING = 0;
	public static final int TEXT_REPR_TEXT = 1;

	public static void writeTextUntagged(PacketByteBuf buf, String string) {
		byte[] bs = string.getBytes(StandardCharsets.US_ASCII);
		if (bs.length > 255) {
			bs[253] = '.';
			bs[254] = '.';
			bs[255] = '.';
		}
		int l = Math.min(bs.length, 255);
		buf.writeByte(l);
		buf.writeBytes(bs, 0, l);
	}

	public static void writeText(PacketByteBuf buf, String string) {
		buf.writeByte(TEXT_REPR_STRING);
		writeTextUntagged(buf, string);
	}

	public static void writeTextUntagged(PacketByteBuf buf, Text text) {
		buf.writeText(text);
	}

	public static void writeText(PacketByteBuf buf, Text text) {
		buf.writeByte(TEXT_REPR_TEXT);
		writeTextUntagged(buf, text);
	}

	public static String readTextReprString(PacketByteBuf buf) {
		int l = buf.readUnsignedByte();
		var s = buf.toString(buf.readerIndex(), l, StandardCharsets.US_ASCII);
		buf.readerIndex(buf.readerIndex() + l);
		return s;
	}

	public static Text readTextReprText(PacketByteBuf buf) {
		return buf.readText();
	}

	public static StringOrText readText(PacketByteBuf buf) {
		byte tag = buf.readByte();
		Jacksoning.LOGGER.info("Received a type:" + tag + " text");
		return switch (tag) {
			case TEXT_REPR_STRING -> StringOrText.of(readTextReprString(buf));
			case TEXT_REPR_TEXT -> StringOrText.of(readTextReprText(buf));
			default -> throw new DecoderException("Unrecognized string or text type: " + tag);
		};
	}
}
