package dev.pfaff.jacksoning.items.abilities;

import net.minecraft.item.ItemUsageContext;

public sealed interface InteractionTarget {
	public static final Air AIR = Air.AIR;

	record Block(ItemUsageContext context) implements InteractionTarget {}
	record Entity(net.minecraft.entity.Entity entity) implements InteractionTarget {}
	enum Air implements InteractionTarget {
		AIR;
	}
}
