package dev.pfaff.jacksoning.server.shop;

import dev.pfaff.jacksoning.util.codec.CodecException;
import dev.pfaff.jacksoning.util.nbt.Container;
import dev.pfaff.jacksoning.util.nbt.NbtCompound;
import it.unimi.dsi.fastutil.objects.AbstractObject2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.Map;
import java.util.stream.Stream;

import static dev.pfaff.jacksoning.util.nbt.NbtCodecs.NBT_INT;

public final class Shop implements Container {
	private final Map<String, ShopItem> shopItems;
	private final Object2IntMap<String> levels = new Object2IntOpenHashMap<>();

	public Shop(Map<String, ShopItem> shopItems) {
		this.shopItems = shopItems;
	}

	public Stream<Object2IntMap.Entry<ShopItem>> levels() {
		// intellij is truly the state of the art when it comes to code formatting.
		return shopItems.values().stream().map(item -> new AbstractObject2IntMap.BasicEntry<>(item,
																							  levels.getOrDefault(item.id(),
																												  0)));
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
	public void writeNbt(NbtCompound nbt) {
		for (var entry : levels.object2IntEntrySet()) {
			if (entry.getIntValue() == 0) continue;
			nbt.putAs(entry.getKey(), NBT_INT, entry.getIntValue());
		}
	}
}
