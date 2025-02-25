package dev.pfaff.jacksoning.net;

import dev.pfaff.jacksoning.PlayerRole;
import dev.pfaff.jacksoning.util.codec.CodecException;
import io.netty.handler.codec.DecoderException;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.UUID;

import static dev.pfaff.jacksoning.Constants.MOD_ID;

public record PacketPlayerRoleC2S(UUID playerId, PlayerRole role) {
	public static final Identifier CHANNEL = Identifier.of(MOD_ID, "player_role_c2s");

	public PacketByteBuf encode() {
		return PacketByteBufs.create()
				.writeUuid(playerId)
				.writeString(role.asString());
	}

	public static PacketPlayerRoleC2S decode(PacketByteBuf buf) {
		try {
			return new PacketPlayerRoleC2S(buf.readUuid(), PlayerRole.STRING_CODEC.fromR(buf.readString()));
		} catch (CodecException e) {
			throw new DecoderException(e);
		}
	}
}
