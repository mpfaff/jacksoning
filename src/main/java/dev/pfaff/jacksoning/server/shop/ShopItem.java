package dev.pfaff.jacksoning.server.shop;

import dev.pfaff.jacksoning.server.IGamePlayer;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.function.BiConsumer;

public interface ShopItem {
	String id();

	boolean isUpgrade();

	int maxLevel();

	String name(int level);

	ItemStack icon(int level);

	List<String> lore(int level);

	int cost(int level);

	public default void onPurchase(IGamePlayer player, int level) {}

	public default void onTick(IGamePlayer player, int level) {}

	public default ShopItem onTick(BiConsumer<IGamePlayer, Integer> onTick) {
		var self = this;
		return new ShopItem() {
			@Override
			public String id() {
				return self.id();
			}

			@Override
			public boolean isUpgrade() {
				return self.isUpgrade();
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
			public void onPurchase(IGamePlayer player, int level) {
				self.onPurchase(player, level);
			}

			@Override
			public void onTick(IGamePlayer player, int level) {
				self.onTick(player, level);
				onTick.accept(player, level);
			}
		};
	}

	public default ShopItem onPurchase(BiConsumer<IGamePlayer, Integer> onPurchase) {
		var self = this;
		return new ShopItem() {
			@Override
			public String id() {
				return self.id();
			}

			@Override
			public boolean isUpgrade() {
				return self.isUpgrade();
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
			public void onPurchase(IGamePlayer player, int level) {
				self.onPurchase(player, level);
				onPurchase.accept(player, level);
			}

			@Override
			public void onTick(IGamePlayer player, int level) {
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
			public boolean isUpgrade() {
				return true;
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
		};
	}

	public static ShopItem consumable(String id,
									 String name,
									 ItemStack icon,
									 List<String> lore,
									 int cost) {
		return new ShopItem() {
			@Override
			public String id() {
				return id;
			}

			@Override
			public boolean isUpgrade() {
				return false;
			}

			@Override
			public int maxLevel() {
				return 1;
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
		};
	}
}
