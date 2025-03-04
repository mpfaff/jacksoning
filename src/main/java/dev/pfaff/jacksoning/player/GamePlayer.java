package dev.pfaff.jacksoning.player;

import dev.pfaff.jacksoning.Constants;
import dev.pfaff.jacksoning.PlayerRole;
import dev.pfaff.jacksoning.Winner;
import dev.pfaff.jacksoning.mixin.AccessorEntity;
import dev.pfaff.jacksoning.mixin.AccessorFireworkRocketEntity;
import dev.pfaff.jacksoning.mixin.AccessorHungerManager;
import dev.pfaff.jacksoning.server.IGame;
import dev.pfaff.jacksoning.server.JacksoningServer;
import dev.pfaff.jacksoning.server.RoleState;
import dev.pfaff.jacksoning.server.sidebar.ServerSidebar;
import dev.pfaff.jacksoning.util.VecUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import org.slf4j.event.Level;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static dev.pfaff.jacksoning.Config.jacksonBaseHealthBoost;
import static dev.pfaff.jacksoning.Config.jacksonZoneRadius;
import static dev.pfaff.jacksoning.Constants.MODIFIER_JACKSON_MAX_HEALTH;
import static dev.pfaff.jacksoning.Constants.MODIFIER_MISTRESS_MAX_HEALTH;
import static dev.pfaff.jacksoning.player.PlayerData.RESPAWN_TIME_SPAWNED;

public final class GamePlayer {
	public final PlayerData data = new PlayerData();

	public final ServerSidebar sidebar = new ServerSidebar();

	private final HashSet<String> keepModifiers = new HashSet<>();

	private final ServerPlayerEntity player;

	public GamePlayer(ServerPlayerEntity player) {
		this.player = player;
	}

	public static GamePlayer cast(ServerPlayerEntity player) {
		return ((IGamePlayer) player).gamePlayer();
	}

	public PlayerData data() {
		return data;
	}

	public ServerSidebar sidebar() {
		return sidebar;
	}

	public RoleState roleState() {
		return data().roleState;
	}

	public void roleState(RoleState state) {
		data().roleState = state;
	}

	public void setRole(PlayerRole role) {
		JacksoningServer.LOGGER.log(Level.INFO, () -> "Setting role of " + this + " to " + role);
		roleState(role.newState());
	}

	public void setInitRole(PlayerRole role) {
		JacksoningServer.LOGGER.log(Level.INFO, () -> "Setting init role of " + this + " to " + role);
		data().initRole = role;
		setRole(role);
	}

	public ServerPlayerEntity asMc() {
		return player;
	}

	public MinecraftServer server() {
		return Objects.requireNonNull(asMc().getServer());
	}

	public IGame game() {
		return (IGame) server();
	}

	public boolean isReferee() {
		return this.data().role() == PlayerRole.Referee || player.hasPermissionLevel(2);
	}

	public boolean isInsideJacksonZone() {
		var spawnPos = server().getOverworld().getSpawnPos();
		return VecUtil.all(asMc().getBlockPos().subtract(spawnPos), dist -> Math.abs(dist) < jacksonZoneRadius());
	}

	public void applyModifier(RegistryEntry<EntityAttribute> attribute,
							  Identifier id,
							  double value,
							  EntityAttributeModifier.Operation operation) {
		keepModifiers.add(id.getPath());
		if (value == 0.0) {
			return;
		}
		var inst = Objects.requireNonNull(asMc().getAttributeInstance(attribute));
		var modifier = inst.getModifier(id);
		if (modifier != null) {
			if (modifier.value() == value && modifier.operation() == operation) return;
			inst.removeModifier(id);
		}
		modifier = new EntityAttributeModifier(id, value, operation);
		inst.addPersistentModifier(modifier);
	}

	public void onConnect() {
		sidebar.initialize(player);
	}

	public void tick() {
		tickLogic();
		sidebar.tick(player);
	}

	public void tickLogic() {
		//if (asMc().getPos().y < -10 || !asMc().getWorld().getWorldBorder().contains(asMc().getPos().x, asMc().getPos().z)) {
		if (!game().state().isRunning() && asMc().getPos().y < -100) {
			tpSpawn();
		}

		if (roleState().role() != PlayerRole.Referee) {
			applyGameMode(data().isSpawned() ? data().role().gameMode : GameMode.SPECTATOR);
		}

		var sb = player.server.getScoreboard();
		var team = sb.getTeam(roleState().role().mcTeam);
		sb.addScoreHolderToTeam(player.getNameForScoreboard(), team);

		if (game().state().isRunning()) {
			// TODO: https://git.pfaff.dev/michael/jacksoning/issues/5
			if (data().isSpawned()) {
				keepModifiers.clear();
				switch (data().role()) {
					case Jackson -> {
						applyModifier(EntityAttributes.MAX_HEALTH,
									  MODIFIER_JACKSON_MAX_HEALTH,
									  jacksonBaseHealthBoost(),
									  EntityAttributeModifier.Operation.ADD_VALUE);
					}
					case UNLeader -> {
					}
					case Mistress -> {
						applyModifier(EntityAttributes.MAX_HEALTH,
									  MODIFIER_MISTRESS_MAX_HEALTH,
									  -0.5,
									  EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
					}
					default -> {
					}
				}
				var shop = data().roleState.shop();
				if (shop != null) {
					shop.levels().forEach(entry -> entry.left().onTick(this, entry.rightInt()));
				}
				Registries.ATTRIBUTE.streamEntries().forEach(attribute -> {
					var inst = asMc().getAttributeInstance(attribute);
					if (inst == null) return;
					for (var modifier : inst.getModifiers()) {
						if (modifier.id().getNamespace().equals(Constants.MOD_ID) && !keepModifiers.contains(modifier.id().getPath())) {
							inst.removeModifier(modifier.id());
						}
					}
				});
			} else {
				if (--data().respawnTime == RESPAWN_TIME_SPAWNED) {
					respawnPlayer(0);
					if (data().roleState instanceof RoleState.Jackson state) {
						if (!state.spawned) {
							state.spawned = true;
							asMc().addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 20 * 20));
							asMc().addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 20 * 20));
							asMc().addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20 * 20));
							asMc().addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 20 * 20, 2));
						}
					}
				}
			}
		} else {
			data().respawnTime = 0;
		}
	}

	public void applyGameMode(GameMode gameMode) {
		if (gameMode != asMc().interactionManager.getGameMode()) {
			asMc().changeGameMode(gameMode);
		}
	}

	public void onFatalDamage() {
		var g = game();

		if (g.state().isRunning()) {
			switch (data().role()) {
				case Jackson -> {
					if (isInsideJacksonZone()) {
						onJacksonFinalKill();
					}
				}
				case UNLeader -> {
					if (g.players().stream().filter(PlayerRole.UNLeader::matches).count() == 1) {
						var pos = asMc().getPos();
						var itemStack = new ItemStack(Items.FIREWORK_ROCKET);
						for (int i = 0; i < 20; i++) {
							FireworkRocketEntity entity = new FireworkRocketEntity(asMc().getWorld(), pos.x, pos.y, pos.z, itemStack);
							((AccessorFireworkRocketEntity)entity).lifeTime(((AccessorFireworkRocketEntity)entity).lifeTime() + 100);
							var min = -40f;
							var max = -10f;
							entity.setPitch(((AccessorEntity)entity).random().nextFloat() * (max - min) + min);
							entity.setYaw(((AccessorEntity)entity).random().nextFloat() * 360f);
							entity.setVelocity(entity.getRotationVector().multiply(entity.getVelocity().lengthSquared()));
							entity.refreshPositionAfterTeleport(asMc().getPos());
							ProjectileEntity.spawn(entity, asMc().getServerWorld(), itemStack);
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

		respawnPlayer(g.state().respawnCooldown());
	}

	private void onJacksonFinalKill() {
		for (int i = 0; i < 20; i++) {
			LightningEntity entity = EntityType.LIGHTNING_BOLT.create(asMc().getWorld(), SpawnReason.EVENT);
			entity.refreshPositionAfterTeleport(asMc().getPos());
			entity.setCosmetic(true);
			asMc().getWorld().spawnEntity(entity);
		}

		// game over
		game().state().gameOver(server(), Winner.UN);
	}

	public void tpSpawn() {
		var spawnPos = server().getOverworld().getSpawnPos();
		asMc().teleport(server().getOverworld(),
						spawnPos.getX(),
						spawnPos.getY(),
						spawnPos.getZ(),
						Set.of(),
						0f,
						0f,
						true);
	}

	public void respawnPlayer(int delay) {
		data().respawnTime = delay == 0 ? -1 : delay;
		asMc().clearStatusEffects();
		asMc().setOnFire(false);
		asMc().getHungerManager().setFoodLevel(20);
		asMc().getHungerManager().setSaturationLevel(5f);
		((AccessorHungerManager) asMc().getHungerManager()).setExhaustion(0f);
		tickLogic();
		if (game().state().isRunning()) {
			tpSpawn();
		}
		asMc().setHealth(asMc().getMaxHealth());
	}

	static final List<RawFilteredPair<Text>> BOOK_PAGES = List.of(
		RawFilteredPair.of(Text.of("STEP 1:\nPLACING THE TURRET\nright click the turret on the block you want to turret on. \n\nThe turret must rest on the top of blocks. they cannot stick to the bottom or sides of blocks.\n\nTurrets obey gravity,\nand can ride minecart")),
		RawFilteredPair.of(Text.of("STEP 2:\nMOVING THAT GEAR UP\nShift + right click the \nturret to access it's \nsettings. \nClick \"dismantle turret\" to move it. It will drop as an item. It will drop it's inventory. It will keep it's hp.")),
		RawFilteredPair.of(Text.of("STEP 3:\nTARGETING JACKSON\nShift + right click the turret to access settings. Type \"player\" (caps sensitive) into the text box, and press \"add new entity type to target list\". The text box should empty itself. Then press \"claim this turret\" to prevent hijacking.")),
		RawFilteredPair.of(Text.of("STEP 4:\nAMMO\nThe turrets require ammo. \n\nCobble turrets need cobblestone and Brick turrets need bricks (crafting material, not block).\n\nright click the turrets to access their ammo storage and fill them.")),
		RawFilteredPair.of(Text.of("STEP 5:\nREPAIRING\nShift + right click the turret while holding a titanium ingot to repair the turret."))
	);

	public void giveKit() {
		var inv = asMc().getInventory();
		inv.clear();
		switch (data().role()) {
			case UNLeader -> {
				inv.armor.set(3, new ItemStack(Items.LEATHER_HELMET));
				inv.armor.set(2, new ItemStack(Items.LEATHER_CHESTPLATE));
				inv.armor.set(1, new ItemStack(Items.LEATHER_LEGGINGS));
				inv.armor.set(0, new ItemStack(Items.LEATHER_BOOTS));
				var book = new ItemStack(Items.WRITTEN_BOOK);
				book.set(DataComponentTypes.WRITTEN_BOOK_CONTENT, new WrittenBookContentComponent(RawFilteredPair.of("TURRET MANUAL"), "samplest", 0, BOOK_PAGES, true));
				inv.insertStack(book);
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
