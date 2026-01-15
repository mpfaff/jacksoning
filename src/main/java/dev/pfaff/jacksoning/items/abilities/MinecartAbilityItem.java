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

public final class MinecartAbilityItem extends AbilityItem {
	public MinecartAbilityItem(Settings settings) {
		super(settings);
	}

	@Override
	public Item getPolymerItem(ItemStack itemStack, PacketContext packetContext) {
		return Items.MINECART;
	}

	@Override
	public ActionResult useAbility(ServerWorld world, ServerPlayerEntity user, ItemStack stack, InteractionTarget target) {
		//return ActionResult.FAIL;
		ArmorStandEntity customnamepenisman = new ArmorStandEntity(world, user.getX(), user.getY(),user.getZ());
		customnamepenisman.setCustomName(Text.of("temu minecart"));
		world.spawnEntity(customnamepenisman);
		return super.useAbility(world, user, stack, target);
	}
}
