package dev.pfaff.jacksoning.server.sidebar;

import dev.pfaff.jacksoning.packet.UpdateUIPacket;
import dev.pfaff.jacksoning.sidebar.SidebarCommand;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public final class CustomSidebarImpl extends SidebarImpl {
	@Override
	public boolean truncateIsLossy() {
		return false;
	}

	@Override
	public void initialize(ServerPlayerEntity player) {
	}

	@Override
	public void sendUpdates(ServerPlayerEntity p, List<SidebarCommand> commands) {
		ServerPlayNetworking.send(p, new UpdateUIPacket(commands));
	}
}
