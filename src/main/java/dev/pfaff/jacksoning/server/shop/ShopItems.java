package dev.pfaff.jacksoning.server.shop;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtByte;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.pfaff.jacksoning.server.shop.ShopItem.consumable;
import static dev.pfaff.jacksoning.server.shop.ShopItem.upgrade;

public final class ShopItems {
	private static final List<ItemStack> ARMOR = Stream.of(Items.LEATHER_HELMET,
														   Items.GOLDEN_HELMET,
														   Items.IRON_HELMET,
														   Items.DIAMOND_HELMET,
														   Items.DIAMOND_CHESTPLATE,
														   Items.LEATHER_LEGGINGS,
														   Items.GOLDEN_LEGGINGS,
														   Items.IRON_LEGGINGS,
														   Items.DIAMOND_LEGGINGS,
														   Items.LEATHER_BOOTS,
														   Items.GOLDEN_BOOTS,
														   Items.IRON_BOOTS,
														   Items.DIAMOND_BOOTS)
													   .map(ItemStack::new)
													   .peek(ShopItems::setUnbreakable)
													   .peek(stack -> stack.addEnchantment(Enchantments.BINDING_CURSE,
																						   1))
													   .toList();
	private static final List<ItemStack> TOOLS = Stream.of(Items.WOODEN_SWORD,
														   Items.WOODEN_AXE,
														   Items.WOODEN_PICKAXE,
														   Items.WOODEN_SHOVEL,
														   Items.STONE_SWORD,
														   Items.STONE_AXE,
														   Items.STONE_PICKAXE,
														   Items.STONE_SHOVEL,
														   Items.IRON_SWORD,
														   Items.IRON_AXE,
														   Items.IRON_PICKAXE,
														   Items.IRON_SHOVEL,
														   Items.DIAMOND_SWORD,
														   Items.DIAMOND_AXE,
														   Items.DIAMOND_PICKAXE,
														   Items.DIAMOND_SHOVEL)
													   .map(ItemStack::new)
													   .peek(ShopItems::setUnbreakable)
													   .toList();

	private static final int ARMOR_LEVELS = ARMOR.size() / 4;
	private static final int TOOL_TYPES = 4;
	private static final int TOOL_LEVELS = TOOLS.size() / TOOL_TYPES;

	private static ItemStack setUnbreakable(ItemStack stack) {
		stack.setSubNbt("Unbreakable", NbtByte.of(true));
		return stack;
	}

	private static ShopItem armorUpgrade(int baseCost) {
		return upgrade("armor", List.of(new UpgradeEntry[]{
			new UpgradeEntry("Leather", new ItemStack(Items.LEATHER_HELMET), List.of(), baseCost + 8 * 0),
			new UpgradeEntry("Gold", new ItemStack(Items.GOLDEN_HELMET), List.of(), baseCost + 8 * 1),
			new UpgradeEntry("Iron", new ItemStack(Items.IRON_HELMET), List.of(), baseCost + 8 * 2),
			new UpgradeEntry("Diamond", new ItemStack(Items.DIAMOND_HELMET), List.of(), baseCost + 8 * 3),
			})).onPurchase((p, l) -> {
			var armor = p.asMc().getInventory().armor;
			for (int i = 0; i < 4; i++) {
				armor.set(3 - i, ARMOR.get(i * ARMOR_LEVELS).copy());
			}
		});
	}

	private static ShopItem toolUpgrade(List<Integer> costs) {
		return upgrade("tools", List.of(new UpgradeEntry[]{
			new UpgradeEntry("Wood", new ItemStack(Items.WOODEN_SWORD), List.of(), costs.get(0)),
			new UpgradeEntry("Stone", new ItemStack(Items.STONE_SWORD), List.of(), costs.get(1)),
			new UpgradeEntry("Iron", new ItemStack(Items.IRON_SWORD), List.of(), costs.get(2)),
			new UpgradeEntry("Diamond", new ItemStack(Items.DIAMOND_SWORD), List.of(), costs.get(3)),
			})).onPurchase((p, l) -> {
			for (int i = 0; i < 4; i++) {
				p.asMc().giveItemStack(TOOLS.get(i * TOOL_LEVELS).copy());
			}
		});
	}

	private static ShopItem giveItem(String id, String name, ItemStack item, List<String> lore, int cost, int maxUses) {
		var shopItem = switch (maxUses) {
			case -1 -> consumable(id, name, item, lore, cost);
			default -> {
				if (maxUses == 0) throw new IllegalArgumentException("maxUses must be -1 or greater than 0");
				var entry = new UpgradeEntry(name, item, lore, cost);
				var entries = new UpgradeEntry[maxUses];
				Arrays.fill(entries, entry);
				yield upgrade(id, List.of(entries));
			}
		};
		return shopItem.onPurchase((p, l) -> p.asMc().giveItemStack(item));
	}

	private static final List<ShopItem> COMMON = List.of(
		// TODO: tracking compass
		giveItem("flint_and_steel",
				 "Flint and Steel",
				 setUnbreakable(new ItemStack(Items.FLINT_AND_STEEL)),
				 List.of(),
				 2,
				 1),
		giveItem("shield",
				 "Shield",
				 new ItemStack(Items.SHIELD),
				 List.of(),
				 8,
				 -1),
		giveItem("bow",
				 "Bow",
				 setUnbreakable(new ItemStack(Items.BOW)),
				 List.of(),
				 6,
				 -1),
		giveItem("crossbow",
				 "Crossbow",
				 setUnbreakable(new ItemStack(Items.CROSSBOW)),
				 List.of(),
				 6,
				 3),
		giveItem("arrows",
				 "Arrows",
				 setUnbreakable(new ItemStack(Items.ARROW,
											  32)),
				 List.of(),
				 2,
				 -1),
		giveItem("totem_of_undying",
				 "Totem of Undying",
				 new ItemStack(Items.TOTEM_OF_UNDYING),
				 List.of(),
				 32,
				 -1),
		// TODO: cobble drone
		//giveItem("cobble_drone", "Cobble Drone", new ItemStack(), List.of(), 12, -1),
		giveItem("cobblestone",
				 "Cobblestone",
				 new ItemStack(Items.COBBLESTONE, 32),
				 List.of(),
				 1,
				 -1),
		giveItem("bucket",
				 "Bucket",
				 new ItemStack(Items.BUCKET),
				 List.of(),
				 16,
				 -1)
	);

	public static final Map<String, ShopItem> JACKSON = Stream.concat(Stream.of(armorUpgrade(16),
																  toolUpgrade(List.of(0, 2, 9, 20))
																  // TODO: effects
	), COMMON.stream()).collect(Collectors.toUnmodifiableMap(ShopItem::id, Function.identity()));

	public static final Map<String, ShopItem> MISTRESS = Stream.concat(Stream.of(armorUpgrade(24),
																				toolUpgrade(List.of(0, 8, 20, 40))
																				// TODO: effects
	), COMMON.stream()).collect(Collectors.toUnmodifiableMap(ShopItem::id, Function.identity()));
}
