package dev.pfaff.jacksoning.server;

import dev.pfaff.jacksoning.PlayerRole;
import dev.pfaff.jacksoning.Winner;
import dev.pfaff.jacksoning.mixin.AccessorEntity;
import dev.pfaff.jacksoning.mixin.AccessorFireworkRocketEntity;
import dev.pfaff.jacksoning.util.VecUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameMode;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static dev.pfaff.jacksoning.Config.jacksonBaseHealthBoost;
import static dev.pfaff.jacksoning.Config.jacksonZoneRadius;
import static dev.pfaff.jacksoning.Config.respawnCooldown;
import static dev.pfaff.jacksoning.Constants.ITEM_COBBLE_TURRET;
import static dev.pfaff.jacksoning.Constants.MODIFIED_ATTRIBUTES;
import static dev.pfaff.jacksoning.Constants.MODIFIER_ATTACK_DAMAGE_ADDIT;
import static dev.pfaff.jacksoning.Constants.MODIFIER_GROUP;
import static dev.pfaff.jacksoning.Constants.MODIFIER_MAX_HEALTH_ADDIT;
import static dev.pfaff.jacksoning.server.PlayerData.RESPAWN_TIME_SPAWNED;

public interface IGamePlayer {
	public static IGamePlayer cast(ServerPlayerEntity player) {
		return (IGamePlayer) player;
	}

	public PlayerData data();

	public ServerSidebar sidebar();

	public default RoleState roleState() {
		return data().roleState;
	}

	public default void roleState(RoleState state) {
		// TODO: would be better to use event listeners and have listeners on various properties and set update the
		//  change notifiers in there...
		data().roleState = state;
		sidebar().roleChangeNotifier.updateA(state.role());
	}

	public default void setRole(PlayerRole role) {
		JacksoningServer.LOGGER.info("Setting role of " + this + " to " + role);
		roleState(role.newState());
	}

	public default void setInitRole(PlayerRole role) {
		JacksoningServer.LOGGER.info("Setting init role of " + this + " to " + role);
		data().initRole = role;
		setRole(role);
	}

	public default ServerPlayerEntity asMc() {
		return (ServerPlayerEntity) this;
	}

	public default MinecraftServer server() {
		return Objects.requireNonNull(asMc().getServer());
	}

	public default IGame game() {
		return (IGame) server();
	}

	public default boolean isReferee() {
		return this.data().role() == PlayerRole.Referee || ((ServerPlayerEntity) this).hasPermissionLevel(2);
	}

	public default boolean isInsideJacksonZone() {
		var spawnPos = server().getOverworld().getSpawnPos();
		return VecUtil.all(asMc().getBlockPos().subtract(spawnPos), dist -> Math.abs(dist) < jacksonZoneRadius());
	}

	default void applyModifier(HashSet<UUID> keep,
							   EntityAttribute attribute,
							   UUID id,
							   double value,
							   EntityAttributeModifier.Operation operation) {
		keep.add(id);
		if (value == switch (operation) {
			case ADDITION -> 0.0;
			case MULTIPLY_BASE, MULTIPLY_TOTAL -> 1.0;
		}) {
			return;
		}
		var inst = Objects.requireNonNull(asMc().getAttributeInstance(attribute));
		var modifier = inst.getModifier(id);
		if (modifier != null) {
			if (modifier.getValue() == value && modifier.getOperation() == operation) return;
			inst.removeModifier(id);
		}
		modifier = new EntityAttributeModifier(id, MODIFIER_GROUP, value, operation);
		inst.addTemporaryModifier(modifier);
	}

	public default void tickJacksoning() {
		//if (asMc().getPos().y < -10 || !asMc().getWorld().getWorldBorder().contains(asMc().getPos().x, asMc().getPos().z)) {
		if (!game().state().isRunning() && asMc().getPos().y < -100) {
			tpSpawn();
		}
		if (game().state().isRunning()) {
			// TODO: https://git.pfaff.dev/michael/jacksoning/issues/5
			applyGameMode(data().isSpawned() ? data().role().gameMode : GameMode.SPECTATOR);
			if (data().isSpawned()) {
				var keep = new HashSet<UUID>();
				switch (data().role()) {
					case Jackson -> {
						applyModifier(keep,
									  EntityAttributes.GENERIC_MAX_HEALTH,
									  MODIFIER_MAX_HEALTH_ADDIT,
									  jacksonBaseHealthBoost(),
									  EntityAttributeModifier.Operation.ADDITION);
					}
					case UNLeader -> {
						applyModifier(keep,
									  EntityAttributes.GENERIC_ATTACK_DAMAGE,
									  MODIFIER_ATTACK_DAMAGE_ADDIT,
									  -4.0,
									  EntityAttributeModifier.Operation.ADDITION);
					}
					default -> {
					}
				}
				for (var attribute : MODIFIED_ATTRIBUTES) {
					var inst = Objects.requireNonNull(asMc().getAttributeInstance(attribute));
					for (var modifier : inst.getModifiers()) {
						if (modifier.getName().equals(MODIFIER_GROUP) && !keep.contains(modifier.getId())) {
							inst.removeModifier(modifier.getId());
						}
					}
				}
			} else {
				if (--data().respawnTime == RESPAWN_TIME_SPAWNED) {
					respawnPlayer(0);
					if (data().roleState instanceof RoleState.Jackson state) {
						if (!state.spawned) {
							state.spawned = true;
							asMc().addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 20 * 20));
							asMc().addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 20 * 20));
							asMc().addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20 * 20));
							asMc().addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 20 * 20, 3));
						}
					}
				}
			}
		} else {
			data().respawnTime = 0;
		}
	}

	public default void applyGameMode(GameMode gameMode) {
		if (gameMode != asMc().interactionManager.getGameMode()) {
			asMc().changeGameMode(gameMode);
		}
	}

	public default void onFatalDamage() {
		var g = game();

		if (g.state().isRunning()) {
			switch (data().role()) {
				case Jackson -> {
					if (isInsideJacksonZone()) {
						for (int i = 0; i < 20; i++) {
							LightningEntity entity = EntityType.LIGHTNING_BOLT.create(asMc().world);
							entity.refreshPositionAfterTeleport(asMc().getPos());
							entity.setCosmetic(true);
							asMc().world.spawnEntity(entity);
						}

						// game over
						g.state().gameOver(server(), Winner.UN);
					}
				}
				case UNLeader -> {
					if (g.players().stream().filter(PlayerRole.UNLeader::matches).count() == 1) {
						var pos = asMc().getPos();
						for (int i = 0; i < 20; i++) {
							FireworkRocketEntity entity = new FireworkRocketEntity(asMc().world, pos.x, pos.y, pos.z, ItemStack.EMPTY);
							((AccessorFireworkRocketEntity)entity).lifeTime(((AccessorFireworkRocketEntity)entity).lifeTime() + 100);
							var min = -40f;
							var max = -10f;
							entity.setPitch(((AccessorEntity)entity).random().nextFloat() * (max - min) + min);
							entity.setYaw(((AccessorEntity)entity).random().nextFloat() * 360f);
							entity.setVelocity(entity.getRotationVector().multiply(entity.getVelocity().lengthSquared()));
							entity.refreshPositionAfterTeleport(asMc().getPos());
							asMc().world.spawnEntity(entity);
						}
						g.state().gameOver(server(), Winner.Jackson);
					} else {
						setRole(PlayerRole.Mistress);
						asMc().getInventory().dropAll();
						giveKit();
					}
				}
				default -> {
				}
			}
		}

		respawnPlayer(respawnCooldown());
	}

	public default void tpSpawn() {
		var spawnPos = server().getOverworld().getSpawnPos();
		asMc().teleport(server().getOverworld(), spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 0f, 0f);
	}

	public default void respawnPlayer(int delay) {
		data().respawnTime = delay == 0 ? -1 : delay;
		asMc().clearStatusEffects();
		asMc().setOnFire(false);
		asMc().getHungerManager().setFoodLevel(20);
		asMc().getHungerManager().setSaturationLevel(5f);
		asMc().getHungerManager().setExhaustion(0f);
		tickJacksoning();
		if (game().state().isRunning()) {
			tpSpawn();
		}
		asMc().setHealth(asMc().getMaxHealth());
	}

	static final List<NbtString> BOOK_PAGES = List.of(
		NbtString.of("{\"text\":\"STEP 1:\\nPLACING THE TURRET\\nright click the turret on the block you want to turret on. \\n\\nThe turret must rest on the top of blocks. they cannot stick to the bottom or sides of blocks.\\n\\nTurrets obey gravity,\\nand can ride minecart\"}"),
		NbtString.of("{\"text\":\"STEP 2:\\nMOVING THAT GEAR UP\\nShift + right click the \\nturret to access it's \\nsettings. \\nClick \\\"dismantle turret\\\" to move it. It will drop as an item. It will drop it's inventory. It will keep it's hp.\"}"),
		NbtString.of("{\"text\":\"STEP 3:\\nTARGETING JACKSON\\nShift + right click the turret to access settings. Type \\\"player\\\" (caps sensitive) into the text box, and press \\\"add new entity type to target list\\\". The text box should empty itself. Then press \\\"claim this turret\\\" to prevent hijacking. \"}"),
		NbtString.of("{\"text\":\"STEP 4:\\nAMMO\\nThe turrets require ammo. \\n\\nCobble turrets need cobblestone and Brick turrets need bricks (crafting material, not block).\\n\\nright click the turrets to access their ammo storage and fill them. \\n\"}"),
		NbtString.of("{\"text\":\"STEP 5:\\nREPAIRING\\nShift + right click the turret while holding a titanium ingot to repair the turret.\"}")
	);

	public default void giveKit() {
		var inv = asMc().getInventory();
		inv.clear();
		switch (data().role()) {
			case UNLeader -> {
				inv.armor.set(3, new ItemStack(Items.LEATHER_HELMET));
				inv.armor.set(2, new ItemStack(Items.LEATHER_CHESTPLATE));
				inv.armor.set(1, new ItemStack(Items.LEATHER_LEGGINGS));
				inv.armor.set(0, new ItemStack(Items.LEATHER_BOOTS));
				var book = new ItemStack(Items.WRITTEN_BOOK);
				book.setSubNbt("title", NbtString.of("TURRET MANUAL"));
				book.setSubNbt("author", NbtString.of("samplest"));
				var pages = new NbtList();
				pages.addAll(BOOK_PAGES);
				book.setSubNbt("pages", pages);
				inv.insertStack(book);
				if (Registry.ITEM.getOrEmpty(ITEM_COBBLE_TURRET).orElse(null) instanceof Item item) {
					inv.insertStack(new ItemStack(item));
				}
				inv.insertStack(new ItemStack(Items.COBBLESTONE, 64));
				inv.insertStack(new ItemStack(Items.COBBLESTONE, 64));
				inv.insertStack(new ItemStack(Items.COBBLESTONE, 64));
				inv.insertStack(new ItemStack(Items.STONE_AXE));
				inv.insertStack(new ItemStack(Items.IRON_PICKAXE));
				inv.insertStack(new ItemStack(Items.BREAD, 16));
			}
			case Jackson -> {
				inv.insertStack(new ItemStack(Items.WOODEN_SWORD));
				inv.insertStack(new ItemStack(Items.WOODEN_AXE));
				inv.insertStack(new ItemStack(Items.WOODEN_PICKAXE));
				inv.insertStack(new ItemStack(Items.WOODEN_SHOVEL));
				inv.insertStack(new ItemStack(Items.BREAD, 15));
				inv.insertStack(new ItemStack(Items.COBBLESTONE, 32));
			}
			case Mistress -> {
			}
		}
	}
}
