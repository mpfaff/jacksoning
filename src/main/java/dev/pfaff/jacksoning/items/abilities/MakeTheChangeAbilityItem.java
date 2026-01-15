package dev.pfaff.jacksoning.items.abilities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.MathConstants;
import net.minecraft.util.math.MathHelper;
import xyz.nucleoid.packettweaker.PacketContext;

public final class MakeTheChangeAbilityItem extends AbilityItem {
	private static final String MARKER_TAG = "MakeTheChange";

	public MakeTheChangeAbilityItem(Settings settings) {
		super(settings);
	}

	@Override
	public Item getPolymerItem(ItemStack itemStack, PacketContext packetContext) {
		return Items.ARMOR_STAND;
	}

	@Override
	public ActionResult useAbility(ServerWorld world, ServerPlayerEntity user, ItemStack stack, InteractionTarget target) {
		//var entities = world.getEntitiesByType(TypeFilter.instanceOf(ArmorStandEntity.class), (ArmorStandEntity entity) -> entity.getCommandTags().contains(MARKER_TAG));
		//if (!entities.isEmpty()) {
			//ArmorStandEntity marker = entities.getFirst();
			//var markerX = marker.getX();
			//var markerY = marker.getY();
			//var markerZ = marker.getZ();
//			var markerYaw = marker.getYaw();
//			var markerPitch = marker.getPitch();
//			marker.teleport(world, user.getX(), user.getY(), user.getZ(), Set.of(), user.getYaw(), user.getPitch(), false);
//			user.teleport(world, markerX, markerY, markerZ, Set.of(), markerYaw, markerPitch, false);
//		}
		//ArmorStandEntity customnamepenisman = new ArmorStandEntity(world, user.getX(), user.getY(),user.getZ());
		//customnamepenisman.setCustomName(Text.of("Stand up and Lift, Yourself"));
		//world.spawnEntity(customnamepenisman);
		SnowballEntity MakeTheChangeSnowball = new SnowballEntity(EntityType.SNOWBALL, world);
		MakeTheChangeSnowball.setPos(user.getX(),user.getY()+1.5,user.getZ());
		MakeTheChangeSnowball.setVelocity
				(MathHelper.sin(MathConstants.RADIANS_PER_DEGREE*user.getYaw()*-1)*MathHelper.cos(MathConstants.RADIANS_PER_DEGREE*user.getPitch()*-1),
						MathHelper.sin(MathConstants.RADIANS_PER_DEGREE*user.getPitch()*-1),
						MathHelper.cos(MathConstants.RADIANS_PER_DEGREE*user.getYaw()*-1)*MathHelper.cos(MathConstants.RADIANS_PER_DEGREE*user.getPitch()*-1));
		MakeTheChangeSnowball.setCustomName(Text.of("MakeTheChange"));
		world.spawnEntity(MakeTheChangeSnowball);
		return super.useAbility(world, user, stack, target);
	}
}
