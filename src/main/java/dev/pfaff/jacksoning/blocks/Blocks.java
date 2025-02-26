package dev.pfaff.jacksoning.blocks;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import static dev.pfaff.jacksoning.Constants.MOD_ID;

public final class Blocks {
	private static RegistryKey<Block> keyOfBlock(String name) {
		return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, name));
	}

	public static void initialize() {
	}
}
