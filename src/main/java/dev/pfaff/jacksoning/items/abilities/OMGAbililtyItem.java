package dev.pfaff.jacksoning.items.abilities;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.packettweaker.PacketContext;

import static dev.pfaff.jacksoning.sounds.Sounds.OH_MY_GOD_WERE_DOOMED;

public final class OMGAbililtyItem extends AbilityItem {
	public OMGAbililtyItem(Settings settings) {
		super(settings);
	}

	@Override
	public Item getPolymerItem(ItemStack itemStack, PacketContext packetContext) {
		return Items.SUGAR;
	}

	@Override
	public ActionResult useAbility(ServerWorld world, ServerPlayerEntity user, ItemStack stack, InteractionTarget target) {
		world.playSoundFromEntity(null, user, OH_MY_GOD_WERE_DOOMED, SoundCategory.MASTER, 1.0f, 1.0f);
		user.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 100, 6));
		user.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 400, 5));
		user.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100, 5));
		user.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 100, 1));
		//IDEALLY, THIS WOULD ALSO REMOVE THE USER'S DYES (MERCHANT PACKAGES) AND REPLACE THEM WITH MERCHANT LICENSES
		//I DON'T KNOW HOW TO DO THAT THOUGH.
		return super.useAbility(world, user, stack, target);
	}
}
