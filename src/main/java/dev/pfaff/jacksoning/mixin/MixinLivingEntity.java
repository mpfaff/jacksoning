package dev.pfaff.jacksoning.mixin;

import dev.pfaff.jacksoning.entities.IGameEntity;
import dev.pfaff.jacksoning.server.GameTeam;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity implements IGameEntity {
	public MixinLivingEntity(EntityType<? extends LivingEntity> type, World world) {
		super(type, world);
		throw new AssertionError();
	}

	@Override
	public GameTeam gameTeam() {
		return null;
	}
}
