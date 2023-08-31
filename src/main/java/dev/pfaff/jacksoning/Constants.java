package dev.pfaff.jacksoning;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class Constants {
	public static final Identifier PACKET_UPDATE_UI = new Identifier("jacksoning", "update_ui");
	public static final Identifier TYPE_PLAYER_ROLE = new Identifier("jacksoning", "player_role");
	public static final Identifier ITEM_GROOVE = new Identifier("jacksoning", "groove");
	public static final Identifier BLOCK_SELF_DESTRUCT_BUTTON = new Identifier("jacksoning", "self_destruct_button");

	public static final Text LABEL_ROLE = Text.translatable("label.jacksoning.role").append(": ");
	public static final Text MESSAGE_GAME_OVER_JACKSON_WON = Text.translatable("message.jacksoning.game_over.jackson_won");
	public static final Text MESSAGE_GAME_OVER_UN_WON = Text.translatable("message.jacksoning.game_over.un_won");

	public static final String PERSISTENT_STATE_ID = "jacksoning";
}
