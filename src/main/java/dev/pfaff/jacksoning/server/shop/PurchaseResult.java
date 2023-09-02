package dev.pfaff.jacksoning.server.shop;

import net.minecraft.text.Text;

public enum PurchaseResult {
	Success(null),
	Inconsistency("inconsistency"),
	MaxLevel("max_level"),
	NotEnoughGroove("not_enough_groove"),
	NoSuchItem("no_such_item");

	public final Text text;

	PurchaseResult(String id) {
		this.text = id == null ? null : Text.translatable("message.jacksoning.shop_" + id);
	}
}
