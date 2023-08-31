package dev.pfaff.jacksoning.server;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public interface IGame {
	public GameState state();

	public default List<ServerPlayerEntity> players() {
		return ((MinecraftServer) this).getPlayerManager().getPlayerList();
	}

	public static IGame cast(@NotNull MinecraftServer server) {
		return (IGame) Objects.requireNonNull(server);
	}
}
