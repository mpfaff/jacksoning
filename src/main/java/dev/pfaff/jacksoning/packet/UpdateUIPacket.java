package dev.pfaff.jacksoning.packet;

import dev.pfaff.jacksoning.sidebar.SidebarCommand;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static dev.pfaff.jacksoning.Constants.MOD_ID;

public record UpdateUIPacket(List<SidebarCommand> commands) implements CustomPayload {
	public static final CustomPayload.Id<UpdateUIPacket> ID = new CustomPayload.Id<>(Identifier.of(MOD_ID, "update_ui"));

	public static final PacketCodec<RegistryByteBuf, UpdateUIPacket> CODEC = PacketCodec.of(UpdateUIPacket::write, UpdateUIPacket::read);

	private static UpdateUIPacket read(RegistryByteBuf buf) {
		var commands = new ArrayList<SidebarCommand>();
		while (buf.isReadable()) {
			commands.add(SidebarCommand.fromPacket(buf));
		}
		return new UpdateUIPacket(commands);
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

	private void write(RegistryByteBuf buf) {
		commands.forEach(command -> command.writePacket(buf));
	}
}
