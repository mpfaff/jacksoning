package dev.pfaff.jacksoning.items.abilities;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.packettweaker.PacketContext;

public final class MiningFocusAbilityItem extends AbilityItem {
	public MiningFocusAbilityItem(Settings settings) {
		super(settings);
	}

	@Override
	public Item getPolymerItem(ItemStack itemStack, PacketContext packetContext) {
		return Items.DIAMOND_ORE;
	}

	@Override
	public ActionResult useAbility(ServerWorld world, ServerPlayerEntity user, ItemStack stack, InteractionTarget target) {
		world.playSoundFromEntity(null, user, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.MASTER, 1.0f, 1.0f);
		user.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 200, 225));
		user.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 200, 1));
		user.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100, 0));
		return super.useAbility(world, user, stack, target);
	}
}
