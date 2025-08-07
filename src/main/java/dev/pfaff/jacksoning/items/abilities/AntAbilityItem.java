package dev.pfaff.jacksoning.items.abilities;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.packettweaker.PacketContext;

import static dev.pfaff.jacksoning.sounds.Sounds.BOSS_OF_THE_GYM_COME_ON_LETS_GO;

public final class AntAbilityItem extends AbilityItem {
	public AntAbilityItem(Settings settings) {
		super(settings);
	}

	@Override
	public Item getPolymerItem(ItemStack itemStack, PacketContext packetContext) {
		return Items.DARK_OAK_BUTTON;
	}

	@Override
	public ActionResult useAbility(ServerWorld world, ServerPlayerEntity user, ItemStack stack, InteractionTarget target) {
		return ActionResult.FAIL;
	}
}
