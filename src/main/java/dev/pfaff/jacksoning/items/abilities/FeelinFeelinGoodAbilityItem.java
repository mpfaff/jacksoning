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

import static dev.pfaff.jacksoning.sounds.Sounds.FEELIN_FEELING_GOOD;

public final class FeelinFeelinGoodAbilityItem extends AbilityItem {
	public FeelinFeelinGoodAbilityItem(Settings settings) {
		super(settings);
	}

	@Override
	public Item getPolymerItem(ItemStack itemStack, PacketContext packetContext) {
		return Items.GLISTERING_MELON_SLICE;
	}

	@Override
	public ActionResult useAbility(ServerWorld world, ServerPlayerEntity user, ItemStack stack, InteractionTarget target) {
		world.playSoundFromEntity(null, user, FEELIN_FEELING_GOOD, SoundCategory.MASTER, 1.0f, 1.0f);
		//world.playSound(null, user.getBlockPos(), FEELIN_FEELING_GOOD,);
		user.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 40, 5));
		user.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 40, 5));
		//IDEALLY, THIS WOULD ALSO CREATE PARTICLES AROUND THE USER, SPECIFICALLY "/particle minecraft:ominous_spawning ~ ~1 ~ 1 1 1 0.5 100 force" and "/particle minecraft:end_rod ~ ~1 ~ 1 1 1 0.2 100 force"
		return super.useAbility(world, user, stack, target);
	}
}
