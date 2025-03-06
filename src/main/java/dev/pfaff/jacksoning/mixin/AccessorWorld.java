package dev.pfaff.jacksoning.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityLookup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(World.class)
public interface AccessorWorld {
	@Invoker
	EntityLookup<Entity> invokeGetEntityLookup();
}
