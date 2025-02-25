package dev.pfaff.jacksoning;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;

public final class Constants {
	public static final String MOD_ID = "jacksoning";

	public static final Identifier TYPE_PLAYER_ROLE = Identifier.of(MOD_ID, "player_role");
	public static final Identifier ITEM_GROOVE = Identifier.of(MOD_ID, "groove");

	public static final Text LABEL_ROLE = Text.translatable("label.jacksoning.role").append(": ");
	public static final Text MESSAGE_GAME_OVER_JACKSON_WON = Text.translatable("message.jacksoning.game_over.jackson_won");
	public static final Text MESSAGE_GAME_OVER_UN_WON = Text.translatable("message.jacksoning.game_over.un_won");
	public static final Text MESSAGE_INSIDE_JACKSON_ZONE_TRUE = Text.translatable("message.jacksoning.inside_jackson_zone.true");
	public static final Text MESSAGE_INSIDE_JACKSON_ZONE_FALSE = Text.translatable("message.jacksoning.inside_jackson_zone.false");

	public static final Style GROOVE_VALUE_STYLE = Style.EMPTY.withColor(Formatting.GREEN);
	public static final Style MESSAGE_GROOVE_DROP_NUMBER_STYLE = Style.EMPTY.withColor(Formatting.YELLOW);

	public static final String PERSISTENT_STATE_ID = MOD_ID;

	public static final List<RegistryEntry<EntityAttribute>> MODIFIED_ATTRIBUTES = List.of(EntityAttributes.MAX_HEALTH,
																						   EntityAttributes.MOVEMENT_SPEED,
																						   EntityAttributes.ATTACK_DAMAGE,
																						   EntityAttributes.ARMOR);
	public static final Identifier MODIFIER_JACKSON_MAX_HEALTH = Identifier.of(MOD_ID, "jackson_max_health");
	public static final Identifier MODIFIER_UN_LEADER_ATTACK_DAMAGE = Identifier.of(MOD_ID, "un_leader_attack_damage");
	public static final Identifier MODIFIER_UPGRADE_MAX_HEALTH = Identifier.of(MOD_ID, "6e71dbf1-d60c-41cd-b231-d39fdbec0603");
	public static final Identifier MODIFIER_UPGRADE_SPEED = Identifier.of(MOD_ID, "ba56f216-4447-4c7c-8645-4f5ebb805f15");
	public static final Identifier MODIFIER_UPGRADE_ATTACK_DAMAGE = Identifier.of(MOD_ID, "7aec52ea-6980-4a8c-aa43-1074e7244b11");

	public static final Identifier ITEM_COBBLE_TURRET = Identifier.of("k_turrets", "cobble_turret_item");
}
