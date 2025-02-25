package dev.pfaff.jacksoning.util;

import net.minecraft.network.PacketByteBuf;

import java.nio.charset.StandardCharsets;

/**
 * Some networking utilities.
 * <p>
 * The string utilities optimize by taking advantage of some assumptions, like that the strings will only ever contain
 * ASCII characters.
 */
public final class NetworkUtil {
	public static void writeStringWithTruncation(PacketByteBuf buf, String string) {
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

	public static String readString(PacketByteBuf buf) {
		int l = buf.readUnsignedByte();
		var s = buf.toString(buf.readerIndex(), l, StandardCharsets.US_ASCII);
		buf.readerIndex(buf.readerIndex() + l);
		return s;
	}
}
