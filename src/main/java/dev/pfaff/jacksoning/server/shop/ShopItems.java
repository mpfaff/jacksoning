package dev.pfaff.jacksoning.server.shop;

import dev.pfaff.jacksoning.player.GamePlayer;
import it.unimi.dsi.fastutil.ints.Int2DoubleFunction;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.component.type.UnbreakableComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;
import java.util.stream.Stream;

import static dev.pfaff.jacksoning.Constants.MODIFIER_UPGRADE_ATTACK_DAMAGE;
import static dev.pfaff.jacksoning.Constants.MODIFIER_UPGRADE_MAX_HEALTH;
import static dev.pfaff.jacksoning.Constants.MODIFIER_UPGRADE_SPEED;
import static dev.pfaff.jacksoning.server.shop.ShopItem.consumable;
import static dev.pfaff.jacksoning.server.shop.ShopItem.upgrade;
import static dev.pfaff.jacksoning.util.StreamUtil.intsWithIndex;

public final class ShopItems {
	private static final List<Function<GamePlayer, ItemStack>> ARMOR =
		Stream.of(
				  Items.LEATHER_HELMET,
				  Items.LEATHER_CHESTPLATE,
				  Items.LEATHER_LEGGINGS,
				  Items.LEATHER_BOOTS,

				  Items.GOLDEN_HELMET,
				  Items.GOLDEN_CHESTPLATE,
				  Items.GOLDEN_LEGGINGS,
				  Items.GOLDEN_BOOTS,

				  Items.IRON_HELMET,
				  Items.IRON_CHESTPLATE,
				  Items.IRON_LEGGINGS,
				  Items.IRON_BOOTS,

				  Items.DIAMOND_HELMET,
				  Items.DIAMOND_CHESTPLATE,
				  Items.DIAMOND_LEGGINGS,
				  Items.DIAMOND_BOOTS)
			  .map(ItemStack::new)
			  .peek(ShopItems::setUnbreakable)
			  .map(stackBase -> (Function<GamePlayer, ItemStack>) p -> {
				  var stack = stackBase.copy();
				  stack.addEnchantment(p.asMc()
										.getWorld()
										.getRegistryManager()
										.getOptional(
											RegistryKeys.ENCHANTMENT)
										.get()
										.getEntry(
											Enchantments.BINDING_CURSE.getValue())
										.get(), 1);
				  return stack;
			  })
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
		stack.set(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(true));
		return stack;
	}

	private static ShopItem armorUpgrade(int baseCost) {
		return upgrade("armor", List.of(new UpgradeEntry[]{
			new UpgradeEntry("Leather Armor", new ItemStack(Items.LEATHER_HELMET), List.of(), baseCost + 8 * 0),
			new UpgradeEntry("Golden Armor", new ItemStack(Items.GOLDEN_HELMET), List.of(), baseCost + 8 * 1),
			new UpgradeEntry("Iron Armor", new ItemStack(Items.IRON_HELMET), List.of(), baseCost + 8 * 2),
			new UpgradeEntry("Diamond Armor", new ItemStack(Items.DIAMOND_HELMET), List.of(), baseCost + 8 * 3),
			})).onPurchase((p, l) -> {
			l -= 1;
			var armor = p.asMc().getInventory().armor;
			for (int i = 0; i < 4; i++) {
				armor.set(3 - i, ARMOR.get(l * ARMOR_LEVELS + i).apply(p).copy());
			}
		});
	}

	private static ShopItem toolUpgrade(List<Integer> costs) {
		return upgrade("tools", List.of(new UpgradeEntry[]{
			new UpgradeEntry("Wooden Tools", new ItemStack(Items.WOODEN_SWORD), List.of(), costs.get(0)),
			new UpgradeEntry("Stone Tools", new ItemStack(Items.STONE_SWORD), List.of(), costs.get(1)),
			new UpgradeEntry("Iron Tools", new ItemStack(Items.IRON_SWORD), List.of(), costs.get(2)),
			new UpgradeEntry("Diamond Tools", new ItemStack(Items.DIAMOND_SWORD), List.of(), costs.get(3)),
			})).onPurchase((p, l) -> {
			l -= 1;
			for (int i = 0; i < 4; i++) {
				p.asMc().giveItemStack(TOOLS.get(l * TOOL_LEVELS + i).copy());
			}
		});
	}

	private static ShopItem statUpgrade(String id,
										IntFunction<String> name,
										ItemStack icon,
										IntFunction<List<String>> lore,
										List<Integer> costs,
										RegistryEntry<EntityAttribute> attribute,
										Identifier modifierId,
										IntToDoubleFunction value,
										EntityAttributeModifier.Operation operation) {
		return upgrade(id,
					   intsWithIndex(costs).map(tup -> {
						   String nameS = name.apply(tup.leftInt());
						   return new UpgradeEntry(nameS,
												   icon,
												   lore.apply(tup.leftInt()),
												   tup.rightInt());
					   }).toList())
			.onTick((p, l) -> p.applyModifier(attribute, modifierId, value.applyAsDouble(l), operation));
	}

	private static String formatRomanNumeral(int i) {
		return switch (i) {
			case 1 -> "I";
			case 2 -> "II";
			case 3 -> "III";
			case 4 -> "IV";
			case 5 -> "V";
			case 6 -> "VI";
			case 7 -> "VII";
			case 8 -> "VIII";
			case 9 -> "IX";
			case 10 -> "X";
			default -> Integer.toString(i);
		};
	}

	private static ShopItem statUpgrade(String id,
										String name,
										ItemStack icon,
										IntFunction<List<String>> lore,
										List<Integer> costs,
										RegistryEntry<EntityAttribute> attribute,
										Identifier modifierId,
										Int2DoubleFunction value,
										EntityAttributeModifier.Operation operation) {
		return statUpgrade(id,
						   i -> i == 0 ? name : name + ' ' + formatRomanNumeral(i + 1),
						   icon,
						   lore,
						   costs,
						   attribute,
						   modifierId,
						   value,
						   operation);
	}

	private static Int2DoubleFunction funcByList(List<Double> list) {
		return i -> list.get(Math.min(i, list.size() - 1));
	}

	private static ShopItem strengthUpgrade(List<Integer> costs) {
		var icon = new ItemStack(Items.POTION);
		icon.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Potions.STRENGTH));
		return statUpgrade("strength",
						   "Strength",
						   icon,
						   i -> List.of(),
						   costs,
						   EntityAttributes.ATTACK_DAMAGE,
						   MODIFIER_UPGRADE_ATTACK_DAMAGE,
						   funcByList(List.of(0.0, 3.0, 6.0)),
						   EntityAttributeModifier.Operation.ADD_VALUE);
	}

	private static ShopItem speedUpgrade(List<Integer> costs) {
		return statUpgrade("speed",
						   "Speed",
						   new ItemStack(Items.FEATHER),
						   i -> List.of(),
						   costs,
						   EntityAttributes.MOVEMENT_SPEED,
						   MODIFIER_UPGRADE_SPEED,
						   funcByList(List.of(0.0, 0.2, 0.4)),
						   EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
	}

	private static ShopItem healthUpgrade(List<Integer> costs) {
		return statUpgrade("health",
						   "Health Boost",
						   new ItemStack(Items.GOLDEN_APPLE),
						   i -> List.of(),
						   costs,
						   EntityAttributes.MAX_HEALTH,
						   MODIFIER_UPGRADE_MAX_HEALTH,
						   funcByList(List.of(0.0, 20.0 * 3)),
						   EntityAttributeModifier.Operation.ADD_VALUE);
	}

	private static ShopItem giveItem(String id, String name, ItemStack item, List<String> lore, int cost, int maxUses) {
		var shopItem = consumable(id, name, item, lore, cost, maxUses);
		return shopItem.onPurchase((p, l) -> p.asMc().giveItemStack(item.copy()));
	}

	private static final List<ShopItem> COMMON = List.of(
		// TODO: tracking compass
		giveItem("flint_and_steel",
				 "Flint and Steel",
				 setUnbreakable(new ItemStack(Items.FLINT_AND_STEEL)),
				 List.of(),
				 2,
				 1),
		giveItem("shield", "Shield", new ItemStack(Items.SHIELD), List.of(), 8, -1),
		giveItem("bow", "Bow", setUnbreakable(new ItemStack(Items.BOW)), List.of(), 6, -1),
		giveItem("crossbow", "Crossbow", setUnbreakable(new ItemStack(Items.CROSSBOW)), List.of(), 6, 3),
		giveItem("arrows", "Arrows", setUnbreakable(new ItemStack(Items.ARROW, 32)), List.of(), 2, -1),
		giveItem("totem_of_undying", "Totem of Undying", new ItemStack(Items.TOTEM_OF_UNDYING), List.of(), 32, -1),
		// TODO: cobble drone
		//giveItem("cobble_drone", "Cobble Drone", new ItemStack(), List.of(), 12, -1),
		giveItem("cobblestone", "Cobblestone", new ItemStack(Items.COBBLESTONE, 32), List.of(), 1, -1),
		giveItem("bucket", "Bucket", new ItemStack(Items.BUCKET), List.of(), 16, -1));

	public static final Shop JACKSON = new Shop(Stream.concat(Stream.of(armorUpgrade(16),
																		toolUpgrade(List.of(0,
																							2,
																							9,
																							20)).initialLevel(1),
																		strengthUpgrade(List.of(8, 24)),
																		speedUpgrade(List.of(3, 32)),
																		healthUpgrade(List.of(44))
																		// TODO: "UN leaders glow for 20 seconds" effect
	), COMMON.stream()).toList());

	public static final Shop MISTRESS = new Shop(Stream.concat(Stream.of(armorUpgrade(24),
																		 toolUpgrade(List.of(0, 8, 20, 40)),
																		 strengthUpgrade(List.of(28, 48)),
																		 speedUpgrade(List.of(10, 36)),
																		 healthUpgrade(List.of(44))
																		 // TODO: "UN leaders glow for 20 seconds" effect
	), COMMON.stream()).toList());
}
