package dev.pfaff.jacksoning.mixin;

import net.minecraft.entity.projectile.FireworkRocketEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FireworkRocketEntity.class)
public interface AccessorFireworkRocketEntity {
	@Accessor(value = "lifeTime")
	int lifeTime();

	@Accessor(value = "lifeTime")
	void lifeTime(int value);
}
