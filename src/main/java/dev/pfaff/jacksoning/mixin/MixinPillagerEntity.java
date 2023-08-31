package dev.pfaff.jacksoning.mixin;

import dev.pfaff.jacksoning.PlayerRole;
import dev.pfaff.jacksoning.server.IGamePlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.PillagerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PillagerEntity.class)
public abstract class MixinPillagerEntity extends IllagerEntity {
	private MixinPillagerEntity() {
		super(null, null);
		throw new AssertionError();
	}

	@Override
	public boolean isTeammate(Entity other) {
		return super.isTeammate(other) || (other instanceof IGamePlayer gp && gp.data().role() == PlayerRole.UNLeader);
	}
}
