package dev.pfaff.jacksoning;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.UUID;

public final class Constants {
	public static final Identifier PACKET_UPDATE_UI = new Identifier("jacksoning", "update_ui");
	public static final Identifier TYPE_PLAYER_ROLE = new Identifier("jacksoning", "player_role");
	public static final Identifier ITEM_GROOVE = new Identifier("jacksoning", "groove");
	public static final Identifier BLOCK_SELF_DESTRUCT_BUTTON = new Identifier("jacksoning", "self_destruct_button");

	public static final Text LABEL_ROLE = Text.translatable("label.jacksoning.role").append(": ");
	public static final Text MESSAGE_GAME_OVER_JACKSON_WON = Text.translatable("message.jacksoning.game_over.jackson_won");
	public static final Text MESSAGE_GAME_OVER_UN_WON = Text.translatable("message.jacksoning.game_over.un_won");
	public static final Text MESSAGE_INSIDE_JACKSON_ZONE_TRUE = Text.translatable("message.jacksoning.inside_jackson_zone.true");
	public static final Text MESSAGE_INSIDE_JACKSON_ZONE_FALSE = Text.translatable("message.jacksoning.inside_jackson_zone.false");

	public static final String PERSISTENT_STATE_ID = "jacksoning";

	public static final List<EntityAttribute> MODIFIED_ATTRIBUTES = List.of(EntityAttributes.GENERIC_MAX_HEALTH,
																			EntityAttributes.GENERIC_MOVEMENT_SPEED,
																			EntityAttributes.GENERIC_ATTACK_DAMAGE,
																			EntityAttributes.GENERIC_ARMOR);
	public static final String MODIFIER_GROUP = "jacksoning";
	public static final UUID MODIFIER_JACKSON_MAX_HEALTH_BUF = UUID.fromString("62a0f540-0883-4b78-8271-80ef2a4f6365");
	public static final UUID MODIFIER_UN_LEADER_ATTACK_DAMAGE_DEBUF = UUID.fromString("62a0f540-0883-4b78-8271-80ef2a4f6365");

	public static final Identifier ITEM_COBBLE_TURRET = new Identifier("k_turrets:cobble_turret_item");
}
