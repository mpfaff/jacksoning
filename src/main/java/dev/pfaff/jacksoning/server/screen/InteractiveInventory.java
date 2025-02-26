package dev.pfaff.jacksoning.server.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public abstract class InteractiveInventory implements Inventory {
	protected final int size;

	public InteractiveInventory(int size) {
		this.size = size;
	}

	@Override
	public final int size() {
		return size;
	}

	@Override
	public final boolean isEmpty() {
		return false;
	}

	protected abstract ItemStack getStackInBounds(int slot);

	@Override
	public final ItemStack getStack(int slot) {
		if (slot < 0 || slot >= size) return ItemStack.EMPTY;
		return getStackInBounds(slot);
	}

	@Override
	public final ItemStack removeStack(int slot, int amount) {
		return ItemStack.EMPTY;
	}

	@Override
	public final ItemStack removeStack(int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public final void setStack(int slot, ItemStack stack) {
	}

	@Override
	public final void markDirty() {
	}

	@Override
	public final boolean canPlayerUse(PlayerEntity player) {
		return true;
	}

	@Override
	public final void clear() {
	}
}
