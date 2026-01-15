package dev.pfaff.jacksoning.items.abilities;

import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.packettweaker.PacketContext;

public final class TheyWillKickYouAndTheyWillBeatYouAbilityItem extends AbilityItem {
	private static final String MARKER_TAG = "TheyWillKickYouAndTheyWillBeatYou";

	public TheyWillKickYouAndTheyWillBeatYouAbilityItem(Settings settings) {
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
		ArmorStandEntity customnamepenisman = new ArmorStandEntity(world, user.getX(), user.getY(),user.getZ());
		customnamepenisman.setCustomName(Text.of("They'll Kick You And They'll Beat You"));
		world.spawnEntity(customnamepenisman);
		return super.useAbility(world, user, stack, target);
	}
}
