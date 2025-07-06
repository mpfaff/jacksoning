package dev.pfaff.jacksoning.items.abilities;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import xyz.nucleoid.packettweaker.PacketContext;

public final class MiningFocusItem extends AbilityItem {
	public MiningFocusItem(Settings settings) {
		super(settings);
	}

	@Override
	public Item getPolymerItem(ItemStack itemStack, PacketContext packetContext) {
		return Items.FEATHER;
	}

	@Override
	public ActionResult useAbility(ServerWorld world, ServerPlayerEntity user, ItemStack stack) {
		world.playSoundFromEntity(null, user, SoundEvent.of(Identifier.of("entity.blaze.shoot")), SoundCategory.MASTER, 1.0f, 1.0f);
		user.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 200, 225));
		user.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 200, 1));
		user.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100, 0));
		return super.useAbility(world, user, stack);
	}
}
