package dev.pfaff.jacksoning.server.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerSyncHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;

public abstract class InteractiveScreenHandler<I extends InteractiveInventory> extends GenericContainerScreenHandler {
	protected final PlayerInventory playerInv;
	protected final I inv;
	private ScreenHandlerSyncHandler syncHandler;

	public InteractiveScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, I inventory, int rows) {
		super(type, syncId, playerInventory, inventory, rows);
		this.playerInv = playerInventory;
		this.inv = inventory;
	}

	protected final ScreenHandlerSyncHandler syncHandler() {
		return syncHandler;
	}

	/**
	 * @return the new stack for the clicked slot.
	 */
	protected abstract ItemStack onSlotClickInner(int slot, int button, SlotActionType actionType, PlayerEntity player);

	@Override
	public final void updateSyncHandler(ScreenHandlerSyncHandler handler) {
		super.updateSyncHandler(handler);
		this.syncHandler = handler;
	}

	@Override
	public final ItemStack quickMove(PlayerEntity player, int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public final boolean canUse(PlayerEntity player) {
		return true;
	}

	@Override
	public final void onSlotClick(int slot, int button, SlotActionType actionType, PlayerEntity player) {
		if (slot < 0) return;
		var slotStack = onSlotClickInner(slot, button, actionType, player);
		this.syncHandler.updateSlot(this, slot, slotStack);
		this.syncHandler.updateCursorStack(this, ItemStack.EMPTY);
	}

	protected final void syncPlayerInventory() {
		// update the player's inventory on the client so that they see any items they have received, also to reset any
		// changes they might have made (client-side).
		int invSize = inv.size();
		for (int i = invSize; i < invSize + playerInv.size(); i++) {
			this.syncHandler.updateSlot(this, i, getSlot(i).getStack());
		}
	}

	@Override
	public final void sendContentUpdates() {
	}
}
