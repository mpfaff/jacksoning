package dev.pfaff.jacksoning.blocks;

import dev.pfaff.jacksoning.Constants;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.registry.Registry;

public final class Blocks {
	public static final SelfDestructButtonBlock SELF_DESTRUCT_BUTTON = new SelfDestructButtonBlock(FabricBlockSettings.of(
		Material.DECORATION).noCollision().strength(-1.0f, 3600000.0f).dropsNothing());

	public static void register() {
		Registry.register(Registry.BLOCK, Constants.BLOCK_SELF_DESTRUCT_BUTTON, SELF_DESTRUCT_BUTTON);
		Registry.register(Registry.ITEM,
						  Constants.BLOCK_SELF_DESTRUCT_BUTTON,
						  new BlockItem(SELF_DESTRUCT_BUTTON, new FabricItemSettings().group(ItemGroup.REDSTONE)));
	}
}
