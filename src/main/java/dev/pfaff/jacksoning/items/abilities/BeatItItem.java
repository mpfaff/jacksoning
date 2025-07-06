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

public final class BeatItItem extends AbilityItem {
	public BeatItItem(Settings settings) {
		super(settings);
	}

	@Override
	public Item getPolymerItem(ItemStack itemStack, PacketContext packetContext) {
		return Items.FEATHER;
	}

	@Override
	public ActionResult useAbility(ServerWorld world, ServerPlayerEntity user, ItemStack stack, InteractionTarget target) {
		world.playSoundFromEntity(null, user, BOSS_OF_THE_GYM_COME_ON_LETS_GO, SoundCategory.MASTER, 1.0f, 1.0f);
        //Waits a certain length of time
		//setblock -57 -33 27 minecraft:waxed_copper_bulb
		//IF upgrade "It doesn't matter whos wrong or who's right" is active:/tp @e[x=-50 , y=-33.00, z=37, dx=-13, dy=8, dz=-11, type=!minecraft:player, tag=!Shop] -60 -30 12
		//IF previous line teleports something: world.playSoundFromEntity(null,user, SoundEvent.of(Identifier.of("custom.beatit_doesntmatter")), SoundCategory.MASTER, 1.0F, 1.0F);
		//setblock -58 -31 27 minecraft:waxed_copper_bulb
		//setblock -57 -33 36 minecraft:waxed_copper_bulb
		//setblock -53 -33 31 minecraft:waxed_copper_bulb
		//setblock -57 -31 27 minecraft:stone_pressure_plate
		//setblock -57 -31 27 minecraft:stone_pressure_plate
		//setblock -53 -31 31 minecraft:stone_pressure_plate
		//execute at @e[tag=BeatIt] run tp @a[team= UN,distance=..16] -59 -31 34
		//if above was sucessful: /execute at @e[tag=BeatIt] run tp @p[tag=Michael] -59 -31 34
		//setblock -52 -33 31 minecraft:waxed_copper_bulb
		//setblock -58 -33 36 minecraft:waxed_copper_bulb

		//wait 4 ticks
		//setblock -52 -31 31 minecraft:waxed_copper_bulb
		//setblock -58 -31 36 minecraft:waxed_copper_bulb
		// IF upgrade is active:
		// 	/clone 8 -14 45 39 -23 56 -76 -31 26 filtered minecraft:stone_bricks
		//	/clone 8 -14 45 39 -23 56 -76 -31 26 filtered minecraft:stone_brick_slab
		//	/clone 8 -14 45 39 -23 56 -76 -31 26 filtered minecraft:stone_brick_stairs

		return super.useAbility(world, user, stack, target);
	}
}
