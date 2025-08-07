package dev.pfaff.jacksoning.items;

import dev.pfaff.jacksoning.items.abilities.*;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

import static dev.pfaff.jacksoning.Constants.MOD_ID;
import static dev.pfaff.jacksoning.items.abilities.AbilityItem.REPAIR_TIME;
import static net.minecraft.component.DataComponentTypes.DAMAGE;
import static net.minecraft.component.DataComponentTypes.MAX_DAMAGE;

public final class Items {
	public static final Item CURRENCY = net.minecraft.item.Items.EMERALD;

	public static final Item ANT = register("ant", AntAbilityItem::new, new Item.Settings()
			.component(MAX_DAMAGE, 1)
			.component(DAMAGE, 0)
			.component(REPAIR_TIME, 20));

	public static final Item BEAT_IT = register("beat_it", BeatItAbilityItem::new, new Item.Settings()
			.component(MAX_DAMAGE, 1)
			.component(DAMAGE, 0)
			.component(REPAIR_TIME, 20 * 60 * 3));

	public static final Item CBT = register("cbt", settings -> new CBTAbilityItem(false, settings), new Item.Settings()
			.component(MAX_DAMAGE, 1)
			.component(DAMAGE, 0)
			.component(REPAIR_TIME, 20 * 60 * 3));

	public static final Item CBB = register("cbb", settings -> new CBTAbilityItem(true, settings), new Item.Settings()
			.component(MAX_DAMAGE, 1)
			.component(DAMAGE, 0)
			.component(REPAIR_TIME, 20 * 60 * 3));

	public static final Item FEELIN_FEELIN_GOOD = register("feelin_feelin_good", FeelinFeelinGoodAbilityItem::new, new Item.Settings()
			.component(MAX_DAMAGE, 1)
			.component(DAMAGE, 0)
			.component(REPAIR_TIME, 20 * 50));

	public static final Item MAN_IN_THE_MIRROR = register("man_in_the_mirror", ManInTheMirrorAbilityItem::new, new Item.Settings()
			.component(MAX_DAMAGE, 1)
			.component(DAMAGE, 0)
			.component(REPAIR_TIME, 20 * 60 * 3));

	public static final Item MINECART = register("minecart", MinecartAbilityItem::new, new Item.Settings()
			.component(MAX_DAMAGE, 1)
			.component(DAMAGE, 0)
			.component(REPAIR_TIME, 20));

	public static final Item MINING_FOCUS = register("mining_focus", MiningFocusAbilityItem::new, new Item.Settings()
			.component(MAX_DAMAGE, 2)
			.component(DAMAGE, 0)
			.component(REPAIR_TIME, 20 * 40));

	public static final Item OH_MY_GOD_WERE_DOOMED = register("oh_my_god_were_doomed", OMGAbililtyItem::new, new Item.Settings()
			.component(MAX_DAMAGE, 1)
			.component(DAMAGE, 0)
			.component(REPAIR_TIME, 20 * 40));

	public static final Item RALLY = register("rally", RallyAbilityItem::new, new Item.Settings()
			.component(MAX_DAMAGE, 1)
			.component(DAMAGE, 0)
			.component(REPAIR_TIME, 20 * 60 * 3));

	public static final Item TEST_ABILITY = register("test_ability", TestAbilityItem::new, new Item.Settings()
			.component(MAX_DAMAGE, 4)
			.component(DAMAGE, 0)
			.component(REPAIR_TIME, 40));

	public static final Item YOW = register("yow", YowAbilityItem::new, new Item.Settings()
			.component(MAX_DAMAGE, 4)
			.component(DAMAGE, 0)
			.component(REPAIR_TIME, 20 * 60 * 3));

	private static RegistryKey<Item> keyOfItem(String name) {
		return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, name));
	}

	private static <T extends Item> T register(String name, Function<Item.Settings, T> itemFactory, Item.Settings settings) {
		var key = keyOfItem(name);
		return Registry.register(Registries.ITEM, key.getValue(), itemFactory.apply(settings.registryKey(key)));
	}

	public static void initialize() {}
}
