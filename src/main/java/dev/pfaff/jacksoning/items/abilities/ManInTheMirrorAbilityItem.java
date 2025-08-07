package dev.pfaff.jacksoning.items.abilities;

import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypeFilter;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.Set;

public final class ManInTheMirrorAbilityItem extends AbilityItem {
	private static final String MARKER_TAG = "ManintheMirror";

	public ManInTheMirrorAbilityItem(Settings settings) {
		super(settings);
	}

	@Override
	public Item getPolymerItem(ItemStack itemStack, PacketContext packetContext) {
		return Items.ARMOR_STAND;
	}

	@Override
	public ActionResult useAbility(ServerWorld world, ServerPlayerEntity user, ItemStack stack, InteractionTarget target) {
		var entities = world.getEntitiesByType(TypeFilter.instanceOf(ArmorStandEntity.class), (ArmorStandEntity entity) -> entity.getCommandTags().contains(MARKER_TAG));
		if (!entities.isEmpty()) {
			ArmorStandEntity marker = entities.getFirst();
			var markerX = marker.getX();
			var markerY = marker.getY();
			var markerZ = marker.getZ();
			var markerYaw = marker.getYaw();
			var markerPitch = marker.getPitch();
			marker.teleport(world, user.getX(), user.getY(), user.getZ(), Set.of(), user.getYaw(), user.getPitch(), false);
			user.teleport(world, markerX, markerY, markerZ, Set.of(), markerYaw, markerPitch, false);
		}
		return super.useAbility(world, user, stack, target);
	}
}
