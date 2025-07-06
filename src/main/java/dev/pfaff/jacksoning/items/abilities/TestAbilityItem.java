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
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
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
		world.playSoundFromEntity(null, user, SoundEvent.of(Identifier.of("custom.ohmygodweredoomed")), SoundCategory.MASTER, 1.0f, 1.0f);
		user.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 10, 5));
		return super.useAbility(world, user, stack);
	}
}
