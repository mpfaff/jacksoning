package dev.pfaff.jacksoning.mixin;

import dev.pfaff.jacksoning.util.codec.DynamicCodecs;
import dev.pfaff.jacksoning.util.codec.IDynamicCodecs;
import net.minecraft.registry.DynamicRegistryManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DynamicRegistryManager.ImmutableImpl.class)
public final class MixinDynamicRegistryManagerImmutableImpl implements IDynamicCodecs {
	@Unique
	private final DynamicCodecs dynamicCodecs = new DynamicCodecs((DynamicRegistryManager) (Object) this);

	@Override
	public DynamicCodecs dynamicCodecs() {
		return dynamicCodecs;
	}
}
