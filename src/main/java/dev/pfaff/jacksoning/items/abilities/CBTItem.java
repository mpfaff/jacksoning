package dev.pfaff.jacksoning.items.abilities;

import dev.pfaff.jacksoning.server.GameTeam;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import xyz.nucleoid.packettweaker.PacketContext;

import static dev.pfaff.jacksoning.player.IGamePlayer.asIGamePlayer;
import static dev.pfaff.jacksoning.sounds.Sounds.CBT_CAST;

public final class CBTItem extends AbilityItem {
	public CBTItem(Settings settings) {
		super(settings);
	}

	@Override
	public Item getPolymerItem(ItemStack itemStack, PacketContext packetContext) {
		return Items.FEATHER;
	}

	@Override
	public ActionResult useAbility(ServerWorld world, ServerPlayerEntity user, ItemStack stack, InteractionTarget target) {
		if (target == InteractionTarget.AIR) {
			// don't waste
			return ActionResult.FAIL;
		}

		boolean any = false;

		switch (target) {
			case InteractionTarget.Block block -> {
				var r = 4.0;
				var d = r * 2;
				var c = block.context().getHitPos();
				var rSq = r * r;
				var targets = world.getEntitiesByType(
					TypeFilter.instanceOf(ServerPlayerEntity.class),
					Box.of(c, d, d, d),
					player -> {
						return asIGamePlayer(player).gameTeam() == GameTeam.UN && player.squaredDistanceTo(c) < rSq;
					}
				);
				if (!targets.isEmpty()) {
					any = true;
					targets.forEach(player -> {
						applyCBT(world, user, player);
					});
				}
			}
			case InteractionTarget.Entity(var entity) when entity instanceof ServerPlayerEntity player && asIGamePlayer(player).gameTeam() == GameTeam.UN -> {
				any = true;
				applyCBT(world, user, player);
			}
			default -> {
			}
		}

		if (any) {
			world.playSoundFromEntity(null, user, CBT_CAST, SoundCategory.MASTER, 1.0f, 1.0f);
		}

		return super.useAbility(world, user, stack, target);
	}

	private static void applyCBT(ServerWorld world, ServerPlayerEntity user, LivingEntity entity) {
		entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 7, 2));
		entity.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 3, 2));
		entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 10, 2));
		entity.damage(world, user.getDamageSources().playerAttack(user), 4);
	}
}
