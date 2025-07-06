package dev.pfaff.jacksoning.player;

import dev.pfaff.jacksoning.entities.IGameEntity;
import dev.pfaff.jacksoning.server.GameTeam;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public interface IGamePlayer extends IGameEntity {
	GamePlayer gamePlayer();

	@Override
	@Nullable
	default GameTeam gameTeam() {
		return gamePlayer().roleState().role().team;
	}

	static IGamePlayer asIGamePlayer(ServerPlayerEntity player) {
		return (IGamePlayer) player;
	}
}
