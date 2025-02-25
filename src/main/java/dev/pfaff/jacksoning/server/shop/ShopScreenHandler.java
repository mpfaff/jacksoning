package dev.pfaff.jacksoning.server.shop;

import dev.pfaff.jacksoning.server.GamePlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerSyncHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class ShopScreenHandler extends GenericContainerScreenHandler {
	private final ShopState shop;
	private final PlayerInventory playerInv;
	private final ShopInventory inv;
	private ScreenHandlerSyncHandler syncHandler;

	public ShopScreenHandler(int syncId, PlayerInventory playerInventory, ShopState shop) {
		super(ScreenHandlerType.GENERIC_9X6, syncId, playerInventory, new ShopInventory(shop, 9 * 6), 6);
		this.shop = shop;
		this.playerInv = playerInventory;
		this.inv = (ShopInventory) this.getInventory();
	}

	@Override
	public void updateSyncHandler(ScreenHandlerSyncHandler handler) {
		super.updateSyncHandler(handler);
		this.syncHandler = handler;
	}

	@Override
	public void syncState() {
		super.syncState();
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}

	@Override
	public void onSlotClick(int slot, int button, SlotActionType actionType, PlayerEntity player) {
		if (slot < 0) return;
		ItemStack slotStack;
		if (handleOnSlotClick(slot, actionType, player)) {
			// success
			slotStack = inv.updateStack(slot);
		} else {
			slotStack = inv.getStack(slot);
		}
		this.syncHandler.updateSlot(this, slot, slotStack);
		this.syncHandler.updateCursorStack(this, ItemStack.EMPTY);
		// update the player's inventory on the client so that they see any items they have received, also to reset any
		// changes they might have made (client-side).
		int invSize = inv.size();
		for (int i = invSize; i < invSize + 9 * 4; i++) {
			this.syncHandler.updateSlot(this, i, getSlot(i).getStack());
		}
	}

	private boolean handleOnSlotClick(int slot, SlotActionType actionType, PlayerEntity player) {
		switch (actionType) {
			case PICKUP -> {
				var info = inv.getShopItemInfo(slot);
				if (info == null) return false;

				var targetLvl = shop.getLevel(info.item()) + 1;
				if (info.lvl() != targetLvl) {
					if (info.lvl() < targetLvl) {
						player.sendMessage(Text.translatable("message.jacksoning.shop.purchase_result.already_purchased").formatted(Formatting.RED), true);
					} else {
						player.sendMessage(Text.translatable("message.jacksoning.shop.purchase_result.must_purchase_lower").formatted(Formatting.RED), true);
					}
					return false;
				}
				var result = shop.purchase(GamePlayer.cast((ServerPlayerEntity) player), info.item().id());
				if (result != PurchaseResult.Success) {
					player.sendMessage(result.text.copy().formatted(Formatting.RED), true);
					return false;
				}
				return true;
			}
			default -> {
			}
		}
		return false;
	}

	@Override
	public void sendContentUpdates() {
	}
}
