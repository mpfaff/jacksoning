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

	public static final Text LABEL_ROLE = Text.translatable("label." + MOD_ID + ".role").append(": ");
	public static final Text MESSAGE_GAME_OVER_JACKSON_WON = Text.translatable("message." + MOD_ID + ".game_over.jackson_won");
	public static final Text MESSAGE_GAME_OVER_UN_WON = Text.translatable("message." + MOD_ID + ".game_over.un_won");
	public static final Text MESSAGE_INSIDE_JACKSON_ZONE_TRUE = Text.translatable("message." + MOD_ID + ".inside_jackson_zone.true");
	public static final Text MESSAGE_INSIDE_JACKSON_ZONE_FALSE = Text.translatable("message." + MOD_ID + ".inside_jackson_zone.false");

	public static final Style GROOVE_VALUE_STYLE = Style.EMPTY.withColor(Formatting.GREEN);
	public static final Style MESSAGE_GROOVE_DROP_NUMBER_STYLE = Style.EMPTY.withColor(Formatting.YELLOW);

	public static final String PERSISTENT_STATE_ID = MOD_ID;

	public static final List<RegistryEntry<EntityAttribute>> MODIFIED_ATTRIBUTES = List.of(EntityAttributes.MAX_HEALTH,
																						   EntityAttributes.MOVEMENT_SPEED,
																						   EntityAttributes.ATTACK_DAMAGE,
																						   EntityAttributes.ARMOR);
	public static final Identifier MODIFIER_JACKSON_MAX_HEALTH = Identifier.of(MOD_ID, "jackson_max_health");
	public static final Identifier MODIFIER_MISTRESS_MAX_HEALTH = Identifier.of(MOD_ID, "mistress_max_health");
	public static final Identifier MODIFIER_UPGRADE_MAX_HEALTH = Identifier.of(MOD_ID, "upgrade_max_health");
	public static final Identifier MODIFIER_UPGRADE_SPEED = Identifier.of(MOD_ID, "upgrade_speed");
	public static final Identifier MODIFIER_UPGRADE_ATTACK_DAMAGE = Identifier.of(MOD_ID, "upgrade_attack_damage");
}
