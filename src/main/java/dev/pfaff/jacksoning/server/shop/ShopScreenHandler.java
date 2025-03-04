package dev.pfaff.jacksoning.server.shop;

import dev.pfaff.jacksoning.player.GamePlayer;
import dev.pfaff.jacksoning.server.screen.InteractiveScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static dev.pfaff.jacksoning.Constants.MOD_ID;

public final class ShopScreenHandler extends InteractiveScreenHandler<ShopInventory> {
	private final ShopState shop;

	public ShopScreenHandler(int syncId, PlayerInventory playerInventory, ShopState shop) {
		super(ScreenHandlerType.GENERIC_9X6, syncId, playerInventory, new ShopInventory(shop, 9 * 6), 6);
		this.shop = shop;
	}

	@Override
	public ItemStack onSlotClickInner(int slot, int button, SlotActionType actionType, PlayerEntity player) {
		if (handleOnSlotClick(slot, actionType, player)) {
			// success
			syncPlayerInventory();
			return inv.updateStack(slot);
		} else {
			return inv.getStack(slot);
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
						player.sendMessage(Text.translatable("message." + MOD_ID + ".shop.purchase_result.already_purchased").formatted(Formatting.RED), true);
					} else {
						player.sendMessage(Text.translatable("message." + MOD_ID + ".shop.purchase_result.must_purchase_lower").formatted(Formatting.RED), true);
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
}
