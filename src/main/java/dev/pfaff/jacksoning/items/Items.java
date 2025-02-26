package dev.pfaff.jacksoning.items;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import static dev.pfaff.jacksoning.Constants.MOD_ID;

public final class Items {
	public static RegistryKey<Item> keyOfItem(String name) {
		return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, name));
	}

	public static final Item CURRENCY = net.minecraft.item.Items.EMERALD;

	public static void initialize() {}
}
