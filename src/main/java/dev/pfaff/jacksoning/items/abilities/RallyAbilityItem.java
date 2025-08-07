package dev.pfaff.jacksoning.items.abilities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import xyz.nucleoid.packettweaker.PacketContext;

public final class RallyAbilityItem extends AbilityItem {
	public static final double RADIUS = 8.0;
	public static final double RADIUS_SQ = RADIUS * RADIUS;
	public static final TypeFilter<Entity, LivingEntity> LIVING_ENTITY_TF = TypeFilter.instanceOf(LivingEntity.class);

	public RallyAbilityItem(Settings settings) {
		super(settings);
	}

	@Override
	public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
		return Items.LIGHT_BLUE_BANNER;
	}

	@Override
	protected ActionResult useAbility(ServerWorld world,
									  ServerPlayerEntity user,
									  ItemStack stack,
									  InteractionTarget target) {
		world.getEntitiesByType(LIVING_ENTITY_TF, Box.of(user.getPos(), RADIUS * 2, RADIUS * 2, RADIUS * 2), entity -> {
			if (entity instanceof PlayerEntity) return false;
			if (entity instanceof IronGolemEntity) return false;
			if (entity.squaredDistanceTo(user) >= RADIUS_SQ) return false;
			entity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 20 * 5, 2));
			return false;
		});
		return super.useAbility(world, user, stack, target);
	}
}
