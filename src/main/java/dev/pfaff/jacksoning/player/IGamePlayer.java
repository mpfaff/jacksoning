package dev.pfaff.jacksoning.player;

import dev.pfaff.jacksoning.entities.IGameEntity;
import dev.pfaff.jacksoning.server.GameTeam;

public interface IGamePlayer extends IGameEntity {
	GamePlayer gamePlayer();

	@Override
	default GameTeam gameTeam() {
		return gamePlayer().roleState().role().team;
	}
}
