package dev.pfaff.jacksoning.entities;

import dev.pfaff.jacksoning.server.GameTeam;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface IGameEntity {
	@Nullable
	GameTeam gameTeam();

	static IGameEntity cast(LivingEntity entity) {
		return (IGameEntity) entity;
	}
}
