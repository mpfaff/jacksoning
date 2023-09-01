package dev.pfaff.jacksoning.server.shop;

import net.minecraft.item.ItemStack;

import java.util.List;

public record UpgradeEntry(String name, ItemStack icon, List<String> lore, int cost) {}
