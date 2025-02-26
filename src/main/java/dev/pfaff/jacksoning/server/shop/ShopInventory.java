package dev.pfaff.jacksoning.server.shop;

import dev.pfaff.jacksoning.Jacksoning;
import dev.pfaff.jacksoning.server.screen.InteractiveInventory;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.event.Level;

import java.util.ArrayList;
import java.util.List;

import static dev.pfaff.jacksoning.Constants.GROOVE_VALUE_STYLE;

public final class ShopInventory extends InteractiveInventory {
	private final ShopState shop;
	private final ItemStack[] stacks;

	public ShopInventory(ShopState shop, int size) {
		super(size);

		this.shop = shop;

		var stacks = new ItemStack[size];
		for (int i = 0; i < size; i++) {
			stacks[i] = generateStack(i);
		}
		this.stacks = stacks;
	}

	private ItemStack generateStack(int slot) {
		var info = getShopItemInfo(slot);
		if (info == null) return ItemStack.EMPTY;
		var item = info.item();
		ItemStack stack = item.icon(info.lvl()).copy();
		stack.remove(DataComponentTypes.ENCHANTMENTS);
		if (info.lvl() <= shop.getLevel(item)) {
			stack = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);
			//var nbt = stack.getOrCreateNbt();
			//net.minecraft.nbt.NbtList list;
			//if (!nbt.contains(ENCHANTMENTS_KEY, net.minecraft.nbt.NbtElement.LIST_TYPE)) {
			//	list = new net.minecraft.nbt.NbtList();
			//	nbt.put(ENCHANTMENTS_KEY, list);
			//} else {
			//	list = nbt.getList(ENCHANTMENTS_KEY, net.minecraft.nbt.NbtElement.COMPOUND_TYPE);
			//}
			//list.add(EnchantmentHelper.createNbt(ENCHANTMENT_DUMMY, 0));
		}
		stack.setCount(1);
		var name = item.name(info.lvl());
		stack.set(DataComponentTypes.CUSTOM_NAME, Text.literal(name));
		stack.set(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);
		stack.remove(DataComponentTypes.ATTRIBUTE_MODIFIERS);
		int cost = item.cost(info.lvl());
		var lore = new ArrayList<Text>();
		for (var line : item.lore(info.lvl())) {
			lore.add(Text.of(line));
		}
		lore.add(Text.of(""));
		lore.add(Text.literal("Cost: ").append(Text.literal(cost + " groove").setStyle(GROOVE_VALUE_STYLE)));
		stack.set(DataComponentTypes.LORE, new LoreComponent(lore));
		return stack;
	}

	private static final int INNER_COLS = 9 - 2;
	private static final List<List<Integer>> INNER_LAYOUT = List.of(
		List.of(-1, -1, -1, -1, -1, -1, -1),
		List.of(-1, -1, -1, 0, -1, -1, -1),
		List.of(-1, -1, 0, -1, 1, -1, -1),
		List.of(-1, 0, -1, 1, -1, 2, -1),
		List.of(0, -1, 1, -1, 2, -1, 3),
		List.of(0, 1, 2, -1, 3, 4, -1),
		List.of(0, 1, 2, -1, 3, 4, 5),
		List.of(0, 1, 2, 3, 4, 5, 6)
	);

	public record ItemAndLevel(@NotNull ShopItem item, int lvl) {}

	@Nullable
	public ItemAndLevel getShopItemInfo(int slot) {
		int row = slot / 9;
		int col = slot % 9;
		if (row >= 1 && row < 5 && col >= 1 && col < 8) { // if not the outer ring
			row -= 1;
			col -= 1;
			slot = row * INNER_COLS + col;

			var items = shop.shop().tieredItems;
			if (items.size() > INNER_COLS) return null;
			int index = INNER_LAYOUT.get(items.size()).get(col);
			if (index == -1) return null;
			var item = items.get(index);
			int lvl = row + 1;
			if (item.isOverMaxLevel(lvl)) return null;
			return new ItemAndLevel(item, lvl);
		}
		int index;
		if (row == 0) {
			index = col;
		} else if (col == 8) {
			index = 9 + row - 1;
		} else if (row == 5) {
			index = 9 + 6 + 9 - col - 1;
		} else if (col == 0) {
			index = 9 + 6 + 9 + 6 - row - 1;
		} else {
			return null;
		}
		var items = shop.shop().nonTieredItems;
		if (index >= items.size()) return null;
		if (index < 0) {
			Jacksoning.LOGGER.log(Level.ERROR, "negative index? slot is " + slot);
			return null;
		}
		var item = items.get(index);
		return new ItemAndLevel(item, shop.getLevel(item) + 1);
	}

	@Override
	public ItemStack getStackInBounds(int slot) {
		return stacks[slot];
	}

	public ItemStack updateStack(int slot) {
		if (slot < 0 || slot >= size) return ItemStack.EMPTY;
		return stacks[slot] = generateStack(slot);
	}
}
