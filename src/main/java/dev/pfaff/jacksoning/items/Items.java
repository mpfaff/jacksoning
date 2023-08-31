package dev.pfaff.jacksoning.items;

import dev.pfaff.jacksoning.Constants;
import net.minecraft.util.registry.Registry;

public final class Items {
	public static final GrooveItem GROOVE = new GrooveItem();

	public static void register() {
		Registry.register(Registry.ITEM, Constants.ITEM_GROOVE, GROOVE);
	}
}
