package dev.pfaff.jacksoning.items.abilities;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

import static dev.pfaff.jacksoning.Constants.MOD_ID;
import static net.minecraft.component.DataComponentTypes.DAMAGE;
import static net.minecraft.component.DataComponentTypes.MAX_DAMAGE;

public abstract class AbilityItem extends Item implements PolymerItem {
	public static final ComponentType<Integer> REPAIR_TIME = Registry.register(
		Registries.DATA_COMPONENT_TYPE,
		Identifier.of(MOD_ID, "repair_time"),
		ComponentType.<Integer>builder().codec(Codecs.NON_NEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT).build()
	);

	static {
		PolymerComponent.registerDataComponent(REPAIR_TIME);
	}

	public AbilityItem(Settings settings) {
		super(settings.maxCount(1));
		if (!this.getComponents().contains(MAX_DAMAGE)) throw new IllegalArgumentException("Missing required component " + MAX_DAMAGE);
		if (!this.getComponents().contains(DAMAGE)) throw new IllegalArgumentException("Missing required component " + DAMAGE);
		if (!this.getComponents().contains(REPAIR_TIME)) throw new IllegalArgumentException("Missing required component " + REPAIR_TIME);
	}

	protected ActionResult useAbility(ServerWorld world, ServerPlayerEntity user, ItemStack stack) {
		int damage = stack.getDamage() + 1;
		int maxDamage = stack.getMaxDamage();
		if (damage >= maxDamage) {
			int repairTime = stack.getOrDefault(REPAIR_TIME, 0);
			user.getItemCooldownManager().set(stack, repairTime);
			damage = 0;
		}
		stack.setDamage(damage);
		return ActionResult.SUCCESS;
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		if (!world.isClient) {
			ItemStack stack = user.getStackInHand(hand);
			return useAbility((ServerWorld) world, (ServerPlayerEntity) user, stack);
		}
		return super.use(world, user, hand);
	}
}
