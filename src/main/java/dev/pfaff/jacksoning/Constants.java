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

	public static final String TRANSLATION_SIDEBAR_ROLE = MOD_ID + ".sidebar.role";
	public static final Text MESSAGE_GAME_OVER_JACKSON_WON = Text.translatable("message." + MOD_ID + ".game_over.jackson_won");
	public static final Text MESSAGE_GAME_OVER_UN_WON = Text.translatable("message." + MOD_ID + ".game_over.un_won");
	public static final Text MESSAGE_INSIDE_NLR_TRUE = Text.translatable("message." + MOD_ID + ".inside_nlr.true");
	public static final Text MESSAGE_INSIDE_NLR_FALSE = Text.translatable("message." + MOD_ID + ".inside_nlr.false");

	public static final Style GROOVE_VALUE_STYLE = Style.EMPTY.withColor(Formatting.GREEN);

	public static final String PERSISTENT_STATE_ID = MOD_ID;

	public static final List<RegistryEntry<EntityAttribute>> MODIFIED_ATTRIBUTES = List.of(EntityAttributes.MAX_HEALTH,
																						   EntityAttributes.MOVEMENT_SPEED,
																						   EntityAttributes.ATTACK_DAMAGE,
																						   EntityAttributes.ARMOR);
	public static final Identifier MODIFIER_JACKSON_MAX_HEALTH = Identifier.of(MOD_ID, "jackson_max_health");
	public static final Identifier MODIFIER_JACKSON_SPEED = Identifier.of(MOD_ID, "jackson_speed");
	public static final Identifier MODIFIER_MISTRESS_MAX_HEALTH = Identifier.of(MOD_ID, "mistress_max_health");
	public static final Identifier MODIFIER_PSY_MAX_HEALTH = Identifier.of(MOD_ID, "psy_max_health");
	public static final Identifier MODIFIER_PSY_MINING_EFFICIENCY = Identifier.of(MOD_ID, "psy_mining_efficiency");
	public static final Identifier MODIFIER_PSY_MOVEMENT_SPEED = Identifier.of(MOD_ID, "psy_movement_speed");
	public static final Identifier MODIFIER_PSY_ATTACK_DAMAGE = Identifier.of(MOD_ID, "psy_attack_damage");
	public static final Identifier MODIFIER_UPGRADE_MAX_HEALTH = Identifier.of(MOD_ID, "upgrade_max_health");
	public static final Identifier MODIFIER_UPGRADE_SPEED = Identifier.of(MOD_ID, "upgrade_speed");
	public static final Identifier MODIFIER_UPGRADE_ATTACK_DAMAGE = Identifier.of(MOD_ID, "upgrade_attack_damage");
}
