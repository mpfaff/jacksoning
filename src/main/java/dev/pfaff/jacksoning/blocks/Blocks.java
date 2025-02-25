package dev.pfaff.jacksoning.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import static dev.pfaff.jacksoning.Constants.MOD_ID;
import static net.minecraft.block.Blocks.createButtonSettings;
import static net.minecraft.block.Blocks.register;

public final class Blocks {
	private static RegistryKey<Block> keyOfBlock(String name) {
		return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, name));
	}

	public static final Block SELF_DESTRUCT_BUTTON = register(
		keyOfBlock("self_destruct_button"),
		settings -> new SelfDestructButtonBlock(
			BlockSetType.STONE,
			20,
			settings
		),
		createButtonSettings()
			.pistonBehavior(PistonBehavior.BLOCK)
			.strength(-1.0f, 3600000.0f)
			.dropsNothing()
	);

	public static void initialize() {
	}
}
