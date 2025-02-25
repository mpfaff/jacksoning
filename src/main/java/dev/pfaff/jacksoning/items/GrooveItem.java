package dev.pfaff.jacksoning.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class GrooveItem extends Item {
	public GrooveItem(Settings settings) {
		super(settings);
	}

	@Override
	public boolean hasGlint(ItemStack stack) {
		return true;
	}
}
