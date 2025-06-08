package dev.pfaff.jacksoning.server;

import dev.pfaff.jacksoning.player.GamePlayer;
import dev.pfaff.jacksoning.player.PlayerCounts;
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

	public default PlayerCounts countPlayers() {
		int unLeaderCount = 0;
		int mistressCount = 0;
		for (var p : players()) {
			switch (GamePlayer.cast(p).roleState().role()) {
				case UNLeader -> unLeaderCount++;
				case Mistress -> mistressCount++;
				default -> {
				}
			}
		}
		return new PlayerCounts(unLeaderCount, mistressCount);
	}

	public static IGame cast(@NotNull MinecraftServer server) {
		return (IGame) Objects.requireNonNull(server);
	}
}
