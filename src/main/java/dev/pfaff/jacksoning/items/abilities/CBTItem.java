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

public final class CBTItem extends AbilityItem {
	public CBTItem(Settings settings) {
		super(settings);
	}

	@Override
	public Item getPolymerItem(ItemStack itemStack, PacketContext packetContext) {
		return Items.FEATHER;
	}

	@Override
	public ActionResult useAbility(ServerWorld world, ServerPlayerEntity user, ItemStack stack) {
		world.playSoundFromEntity(null,user, SoundEvent.of(Identifier.of("custom.cbtcast")), SoundCategory.MASTER, 1.0F, 1.0F);
		//execute at @e[type= minecraft:bat, name="CBT"] run summon minecraft:armor_stand ~ ~ ~ {Invulnerable:1b,Tags:[CBT,"New"]}
		//kill @e[name=CBT]
		//probably don't use the bat named cbt or armor stands when translating.
		//execute at @e[tag=CBT] run effect give @p[team= UN, distance=..4] minecraft:slow_falling 7 2
		//execute at @e[tag=CBT] run execute as @p[distance=..4, team= UN] run damage @s 4 minecraft:player_attack by @p[team= MJ]
		//execute at @e[tag=CBT] run effect give @p[distance=..4, team= UN] minecraft:levitation 3 2
		//execute at @e[tag=CBT] run effect give @p[distance=..4, team= UN] minecraft:weakness 10 2
		//kill @e[tag=CBT]

		return super.useAbility(world, user, stack);
	}
}
