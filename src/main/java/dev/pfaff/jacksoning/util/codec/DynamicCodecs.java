package dev.pfaff.jacksoning.util.codec;

import net.minecraft.entity.Entity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public final class DynamicCodecs {
	public DynamicCodecs(DynamicRegistryManager registryManager) {
	}

	public static DynamicCodecs get(DynamicRegistryManager registryManager) {
		return ((IDynamicCodecs) registryManager).dynamicCodecs();
	}

	public static DynamicCodecs get(MinecraftServer server) {
		return get(server.getRegistryManager());
	}

	public static DynamicCodecs get(World world) {
		return get(world.getRegistryManager());
	}

	public static DynamicCodecs get(Entity entity) {
		return get(entity.getRegistryManager());
	}
}
