package dev.pfaff.jacksoning.items.abilities;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import xyz.nucleoid.packettweaker.PacketContext;

public final class YowAbilityItem extends AbilityItem {
	public YowAbilityItem(Settings settings) {
		super(settings);
	}

	@Override
	public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
		return Items.CREEPER_HEAD;
	}

	@Override
	protected ActionResult useAbility(ServerWorld world,
									  ServerPlayerEntity user,
									  ItemStack stack,
									  InteractionTarget target) {
		world.createExplosion(
			user,
			user.getDamageSources().explosion(user, user),
			new ExplosionBehavior() {
				@Override
				public boolean shouldDamage(Explosion explosion, Entity entity) {
					return entity != user && super.shouldDamage(explosion, entity);
				}
			},
			user.getX(),
			user.getY(),
			user.getZ(),
			4f,
			false,
			World.ExplosionSourceType.MOB
		);

		return super.useAbility(world, user, stack, target);
	}
}
