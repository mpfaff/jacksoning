package dev.pfaff.jacksoning.items.abilities;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.packettweaker.PacketContext;

public final class TestAbilityItem extends AbilityItem {
	public TestAbilityItem(Settings settings) {
		super(settings);
	}

	@Override
	public Item getPolymerItem(ItemStack itemStack, PacketContext packetContext) {
		return Items.FEATHER;
	}

	@Override
	public ActionResult useAbility(ServerWorld world, ServerPlayerEntity user, ItemStack stack) {
		user.addVelocity(0.0, 1.0, 0.0);
		user.velocityModified = true;
		return super.useAbility(world, user, stack);
	}
}
