package dev.pfaff.jacksoning.server.shop;

import dev.pfaff.jacksoning.player.GamePlayer;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.function.BiConsumer;

public interface ShopItem {
	String id();

	boolean isTiered();

	int initialLevel();

	int maxLevel();

	default boolean isMaxLevel(int level) {
		return maxLevel() != -1 && level >= maxLevel();
	}

	default boolean isOverMaxLevel(int level) {
		return maxLevel() != -1 && level > maxLevel();
	}

	String name(int level);

	ItemStack icon(int level);

	List<String> lore(int level);

	int cost(int level);

	public void onPurchase(GamePlayer player, int level);

	public void onTick(GamePlayer player, int level);

	public default ShopItem initialLevel(int level) {
		var self = this;
		return new ShopItem() {
			@Override
			public String id() {
				return self.id();
			}

			@Override
			public boolean isTiered() {
				return self.isTiered();
			}

			@Override
			public int initialLevel() {
				return level;
			}

			@Override
			public int maxLevel() {
				return self.maxLevel();
			}

			@Override
			public String name(int level) {
				return self.name(level);
			}

			@Override
			public ItemStack icon(int level) {
				return self.icon(level);
			}

			@Override
			public List<String> lore(int level) {
				return self.lore(level);
			}

			@Override
			public int cost(int level) {
				return self.cost(level);
			}

			@Override
			public void onPurchase(GamePlayer player, int level) {
				self.onPurchase(player, level);
			}

			@Override
			public void onTick(GamePlayer player, int level) {
				self.onTick(player, level);
			}
		};
	}

	public default ShopItem onTick(BiConsumer<GamePlayer, Integer> onTick) {
		var self = this;
		return new ShopItem() {
			@Override
			public String id() {
				return self.id();
			}

			@Override
			public boolean isTiered() {
				return self.isTiered();
			}

			@Override
			public int initialLevel() {
				return self.initialLevel();
			}

			@Override
			public int maxLevel() {
				return self.maxLevel();
			}

			@Override
			public String name(int level) {
				return self.name(level);
			}

			@Override
			public ItemStack icon(int level) {
				return self.icon(level);
			}

			@Override
			public List<String> lore(int level) {
				return self.lore(level);
			}

			@Override
			public int cost(int level) {
				return self.cost(level);
			}

			@Override
			public void onPurchase(GamePlayer player, int level) {
				self.onPurchase(player, level);
			}

			@Override
			public void onTick(GamePlayer player, int level) {
				self.onTick(player, level);
				onTick.accept(player, level);
			}
		};
	}

	public default ShopItem onPurchase(BiConsumer<GamePlayer, Integer> onPurchase) {
		var self = this;
		return new ShopItem() {
			@Override
			public String id() {
				return self.id();
			}

			@Override
			public boolean isTiered() {
				return self.isTiered();
			}

			@Override
			public int initialLevel() {
				return self.initialLevel();
			}

			@Override
			public int maxLevel() {
				return self.maxLevel();
			}

			@Override
			public String name(int level) {
				return self.name(level);
			}

			@Override
			public ItemStack icon(int level) {
				return self.icon(level);
			}

			@Override
			public List<String> lore(int level) {
				return self.lore(level);
			}

			@Override
			public int cost(int level) {
				return self.cost(level);
			}

			@Override
			public void onPurchase(GamePlayer player, int level) {
				self.onPurchase(player, level);
				onPurchase.accept(player, level);
			}

			@Override
			public void onTick(GamePlayer player, int level) {
				self.onTick(player, level);
			}
		};
	}

	public static ShopItem upgrade(String id, List<UpgradeEntry> entries) {
		return new ShopItem() {
			@Override
			public String id() {
				return id;
			}

			@Override
			public boolean isTiered() {
				return true;
			}

			@Override
			public int initialLevel() {
				return 0;
			}

			@Override
			public int maxLevel() {
				return entries.size();
			}

			@Override
			public String name(int level) {
				return entries.get(level - 1).name();
			}

			@Override
			public ItemStack icon(int level) {
				return entries.get(level - 1).icon();
			}

			@Override
			public List<String> lore(int level) {
				return entries.get(level - 1).lore();
			}

			@Override
			public int cost(int level) {
				return entries.get(level - 1).cost();
			}

			@Override
			public void onPurchase(GamePlayer player, int level) {
				assert level <= maxLevel();
			}

			@Override
			public void onTick(GamePlayer player, int level) {
				assert level <= maxLevel();
			}
		};
	}

	public static ShopItem consumable(String id,
									  String name,
									  ItemStack icon,
									  List<String> lore,
									  int cost,
									  int maxUses) {
		return new ShopItem() {
			@Override
			public String id() {
				return id;
			}

			@Override
			public boolean isTiered() {
				return false;
			}

			@Override
			public int initialLevel() {
				return 0;
			}

			@Override
			public int maxLevel() {
				return maxUses;
			}

			@Override
			public String name(int level) {
				return name;
			}

			@Override
			public ItemStack icon(int level) {
				return icon;
			}

			@Override
			public List<String> lore(int level) {
				return lore;
			}

			@Override
			public int cost(int level) {
				return cost;
			}

			@Override
			public void onPurchase(GamePlayer player, int level) {
			}

			@Override
			public void onTick(GamePlayer player, int level) {
			}
		};
	}
}
