package dev.pfaff.jacksoning.items;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.function.Consumer;

public abstract class AbilityItem extends Item implements PolymerItem {
	private final Item displayItem;
	private final Consumer<ItemStack> displayItemStackCustomizer;

	public AbilityItem(Item displayItem, Consumer<ItemStack> displayItemStackCustomizer, Settings settings) {
		super(settings);
		this.displayItem = displayItem;
		this.displayItemStackCustomizer = displayItemStackCustomizer;
	}

	@Override
	public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
		return displayItem;
	}

	@Override
	public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipType tooltipType, PacketContext context) {
		var stack = PolymerItem.super.getPolymerItemStack(itemStack, tooltipType, context);
		displayItemStackCustomizer.accept(stack);
		return stack;
	}
}
