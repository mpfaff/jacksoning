package dev.pfaff.jacksoning.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rarity;

public final class GrooveItem extends Item {
	public GrooveItem() {
		super(new FabricItemSettings().rarity(Rarity.EPIC).group(ItemGroup.MATERIALS));
	}

	@Override
	public boolean hasGlint(ItemStack stack) {
		return true;
	}
}
