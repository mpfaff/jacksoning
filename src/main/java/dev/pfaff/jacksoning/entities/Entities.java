package dev.pfaff.jacksoning.entities;

import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import static dev.pfaff.jacksoning.Constants.MOD_ID;

public final class Entities {
	public static final EntityType<TurretEntity> TURRET = register("turret", FabricEntityType.Builder.createLiving(
		TurretEntity::new,
		SpawnGroup.MISC,
		builder -> {
			return builder.defaultAttributes(TurretEntity::defaultAttributesBuilder);
		}));

	private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
		var key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, name));
		var type = Registry.register(Registries.ENTITY_TYPE, key.getValue(), builder.build(key));
		PolymerEntityUtils.registerType(type);
		return type;
	}

	public static void initialize() {
	}
}
