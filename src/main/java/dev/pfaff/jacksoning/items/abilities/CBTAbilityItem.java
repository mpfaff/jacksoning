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
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import static dev.pfaff.jacksoning.player.IGamePlayer.asIGamePlayer;
import static dev.pfaff.jacksoning.sounds.Sounds.CBT_CAST;

public final class CBTAbilityItem extends AbilityItem {
	public static final double REGULAR_RADIUS = 4.0;
	public static final double REGULAR_RADIUS_SQ = REGULAR_RADIUS * REGULAR_RADIUS;
	public static final double SUPER_RADIUS = 6.0;
	public static final double SUPER_RADIUS_SQ = SUPER_RADIUS * SUPER_RADIUS;
	private final boolean isSuper;

	public CBTAbilityItem(boolean isSuper, Settings settings) {
		super(settings);
		this.isSuper = isSuper;
	}

	@Override
	public Item getPolymerItem(ItemStack itemStack, PacketContext packetContext) {
		return isSuper ? Items.STONECUTTER : Items.SHEARS;
	}

	@Override
	public ActionResult useAbility(ServerWorld world, ServerPlayerEntity user, ItemStack stack, InteractionTarget target) {
		if (target == InteractionTarget.AIR) {
			// don't waste
			return ActionResult.FAIL;
		}

		boolean any = false;

		if (this.isSuper) {
			var d = SUPER_RADIUS * 2;
			var c = switch (target) {
				case InteractionTarget.Block block -> block.context().getHitPos();
				// TODO: maybe raycast to get a real "hit pos"
				case InteractionTarget.Entity(var entity) -> entity.getPos();
				default -> null;
			};
			if (c == null) return ActionResult.FAIL;
			var targets = world.getEntitiesByType(
					TypeFilter.instanceOf(ServerPlayerEntity.class),
					Box.of(c, d, d, d),
					player -> canTarget(player, c, SUPER_RADIUS_SQ)
			);
			if (!targets.isEmpty()) {
				any = true;
				targets.forEach(player -> {
					applyCBT(world, user, player);
				});
			}
		} else {
			switch (target) {
				case InteractionTarget.Block block -> {
					var d = REGULAR_RADIUS * 2;
					var c = block.context().getHitPos();
					var targets = world.getEntitiesByType(
							TypeFilter.instanceOf(ServerPlayerEntity.class),
							Box.of(c, d, d, d),
							player -> canTarget(player, c, REGULAR_RADIUS_SQ)
					);
					var result = targets.stream().min((a, b) -> Double.compare(a.squaredDistanceTo(c), b.squaredDistanceTo(c)));
					if (result.isPresent()) {
						any = true;
						applyCBT(world, user, result.get());
					}
				}
				case InteractionTarget.Entity(var entity) when entity instanceof ServerPlayerEntity player && canTarget(player, null, 0.0) -> {
					any = true;
					applyCBT(world, user, player);
				}
				default -> {
				}
			}
		}

		if (!any) {
			return ActionResult.FAIL;
		}

		world.playSoundFromEntity(null, user, CBT_CAST, SoundCategory.MASTER, 1.0f, 1.0f);
		return super.useAbility(world, user, stack, target);
	}

	private static boolean canTarget(ServerPlayerEntity player, @Nullable Vec3d centre, double radiusSq) {
		return asIGamePlayer(player).gameTeam() == GameTeam.UN && (centre == null || player.squaredDistanceTo(centre) < radiusSq);
	}

	private static void applyCBT(ServerWorld world, ServerPlayerEntity user, LivingEntity entity) {
		entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 7, 2));
		entity.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 3, 2));
		entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 10, 2));
		entity.damage(world, user.getDamageSources().playerAttack(user), 4);
	}
}
