package dev.pfaff.jacksoning.items.abilities;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import xyz.nucleoid.packettweaker.PacketContext;

public final class OhMyGodWeReDoomedItem extends AbilityItem {
	public OhMyGodWeReDoomedItem(Settings settings) {
		super(settings);
	}

	@Override
	public Item getPolymerItem(ItemStack itemStack, PacketContext packetContext) {
		return Items.FEATHER;
	}

	@Override
	public ActionResult useAbility(ServerWorld world, ServerPlayerEntity user, ItemStack stack) {
		world.playSoundFromEntity(null,user, SoundEvent.of(Identifier.of("custom.ohmygodweredoomed")), SoundCategory.MASTER, 1.0F, 1.0F);
		user.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 10, 5 ));
		return super.useAbility(world, user, stack);
	}
}
