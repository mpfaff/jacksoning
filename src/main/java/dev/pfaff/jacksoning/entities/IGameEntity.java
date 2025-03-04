package dev.pfaff.jacksoning.entities;

import dev.pfaff.jacksoning.server.GameTeam;
import net.minecraft.entity.LivingEntity;

public interface IGameEntity {
	GameTeam gameTeam();

	static IGameEntity cast(LivingEntity entity) {
		return (IGameEntity) entity;
	}
}
