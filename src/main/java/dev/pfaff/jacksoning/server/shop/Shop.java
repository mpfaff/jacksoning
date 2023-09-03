package dev.pfaff.jacksoning.server.shop;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Shop {
	public final Map<String, ShopItem> byId;
	public final List<ShopItem> items;
	public final List<ShopItem> tieredItems;
	public final List<ShopItem> nonTieredItems;

	public Shop(List<ShopItem> items) {
		this.items = items;
		this.byId = items.stream().collect(Collectors.toUnmodifiableMap(ShopItem::id, Function.identity()));
		this.tieredItems = items.stream().filter(ShopItem::isTiered).toList();
		this.nonTieredItems = items.stream().filter(shopItem -> !shopItem.isTiered()).toList();
	}
}
