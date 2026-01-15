package dev.pfaff.jacksoning.items.abilities;

import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.SetBlockCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.packettweaker.PacketContext;

import static dev.pfaff.jacksoning.sounds.Sounds.BEAT_IT_NO_ONE_WANTS_TO_BE_DEFEATED;
import static dev.pfaff.jacksoning.sounds.Sounds.OH_MY_GOD_WERE_DOOMED;

public final class NoOneWantsToBeDefeatedAbililtyItem extends AbilityItem {
	public NoOneWantsToBeDefeatedAbililtyItem(Settings settings) {
		super(settings);
	}

	@Override
	public Item getPolymerItem(ItemStack itemStack, PacketContext packetContext) {
		return Items.SUGAR;
	}

	@Override
	public ActionResult useAbility(ServerWorld world, ServerPlayerEntity user, ItemStack stack, InteractionTarget target) {
		world.playSoundFromEntity(null, user, BEAT_IT_NO_ONE_WANTS_TO_BE_DEFEATED, SoundCategory.MASTER, 1.0f, 1.0f);
		user.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 10, 3));
		ArmorStandEntity customnamepenisman = new ArmorStandEntity(world, user.getX(), user.getY(),user.getZ());
		customnamepenisman.setCustomName(Text.of("No One Wants To Be Defeated!"));
		world.spawnEntity(customnamepenisman);
		return super.useAbility(world, user, stack, target);
	}
}
