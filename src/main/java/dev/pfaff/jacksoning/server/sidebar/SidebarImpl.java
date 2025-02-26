package dev.pfaff.jacksoning.server.sidebar;

import dev.pfaff.jacksoning.sidebar.SidebarCommand;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public abstract class SidebarImpl {
	public abstract boolean truncateIsLossy();

	public abstract void initialize(ServerPlayerEntity player);

	public abstract void sendUpdates(ServerPlayerEntity player, List<SidebarCommand> commands);
}
