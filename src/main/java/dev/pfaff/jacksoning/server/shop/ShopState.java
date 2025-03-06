package dev.pfaff.jacksoning.server.shop;

import dev.pfaff.jacksoning.items.Items;
import dev.pfaff.jacksoning.player.GamePlayer;
import dev.pfaff.jacksoning.util.codec.CodecException;
import dev.pfaff.jacksoning.util.nbt.Container;
import dev.pfaff.jacksoning.util.nbt.NbtCompound;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.function.ObjIntConsumer;

import static dev.pfaff.jacksoning.util.nbt.NbtCodecs.NBT_INT;

public final class ShopState implements Container {
	private final Shop shop;
	private final Object2IntMap<String> levels = new Object2IntOpenHashMap<>();

	public ShopState(Shop shop) {
		this.shop = shop;
	}

	public Shop shop() {
		return shop;
	}

	public int getLevel(ShopItem item) {
		return Math.max(levels.getOrDefault(item.id(), 0), item.initialLevel());
	}

	public void forEachLevel(ObjIntConsumer<ShopItem> consumer) {
		shop.items.forEach(item -> consumer.accept(item, getLevel(item)));
	}

	public PurchaseResult purchase(GamePlayer player, String id) {
		var item = shop.byId.get(id);
		if (item == null) return PurchaseResult.NoSuchItem;
		int lvl = getLevel(item);
		if (item.isMaxLevel(lvl)) return PurchaseResult.MaxLevel;
		int cost = item.cost(lvl+1);
		var inv = player.asMc().getInventory();
		int availableEmeralds = inv.count(Items.CURRENCY);
		if (availableEmeralds < cost) {
			return PurchaseResult.NotEnoughCurrency;
		}
		if (!removeNItem(inv, Items.CURRENCY, cost)) {
			return PurchaseResult.Inconsistency;
		}
		levels.put(id, lvl+1);
		item.onPurchase(player, lvl+1);
		return PurchaseResult.Success;
	}

	private static boolean removeNItem(Inventory inv, Item item, int count) {
		if (count == 0) return true;
		for (int j = 0; j < inv.size(); ++j) {
			ItemStack itemStack = inv.getStack(j);
			if (!itemStack.getItem().equals(item)) continue;
			int stackCount = itemStack.getCount();
			if (stackCount >= count) {
				itemStack.setCount(stackCount - count);
				return true;
			} else {
				inv.removeStack(j);
				count -= stackCount;
			}
		}
		return false;
	}

	public void reset() {
		levels.clear();
	}

	@Override
	public void readNbt(NbtCompound nbt) throws CodecException {
		levels.clear();
		for (var key : nbt.keys()) {
			int i = nbt.getAs(key, NBT_INT);
			if (i != 0) levels.put(key, i);
		}
	}

	@Override
	public void writeNbt(NbtCompound nbt) throws CodecException {
		for (var entry : levels.object2IntEntrySet()) {
			if (entry.getIntValue() == 0) continue;
			nbt.putAs(entry.getKey(), NBT_INT, entry.getIntValue());
		}
	}
}
