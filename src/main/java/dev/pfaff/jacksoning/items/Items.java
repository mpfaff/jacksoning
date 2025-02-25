package dev.pfaff.jacksoning.items;

import dev.pfaff.jacksoning.blocks.Blocks;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import static dev.pfaff.jacksoning.Constants.MOD_ID;
import static net.minecraft.item.Items.register;

public final class Items {
	public static final Item GROOVE = register(keyOfItem("groove"),
											   GrooveItem::new,
											   new Item.Settings().rarity(Rarity.EPIC));
	public static final Item SELF_DESTRUCT_BUTTON = register(Blocks.SELF_DESTRUCT_BUTTON);

	public static RegistryKey<Item> keyOfItem(String name) {
		return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, name));
	}

	public static void initialize() {}
}
