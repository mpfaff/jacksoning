package dev.pfaff.jacksoning.player;

import dev.pfaff.jacksoning.Constants;
import dev.pfaff.jacksoning.PlayerRole;
import dev.pfaff.jacksoning.mixin.AccessorEntity;
import dev.pfaff.jacksoning.mixin.AccessorFireworkRocketEntity;
import dev.pfaff.jacksoning.mixin.AccessorHungerManager;
import dev.pfaff.jacksoning.mixin.AccessorWorld;
import dev.pfaff.jacksoning.server.GameTeam;
import dev.pfaff.jacksoning.server.IGame;
import dev.pfaff.jacksoning.server.RoleState;
import dev.pfaff.jacksoning.server.sidebar.ServerSidebar;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.function.LazyIterationConsumer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import org.slf4j.event.Level;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static dev.pfaff.jacksoning.Constants.MODIFIER_JACKSON_MAX_HEALTH;
import static dev.pfaff.jacksoning.Constants.MODIFIER_JACKSON_SPEED;
import static dev.pfaff.jacksoning.Constants.MODIFIER_MISTRESS_MAX_HEALTH;
import static dev.pfaff.jacksoning.Constants.MODIFIER_PSY_MAX_HEALTH;
import static dev.pfaff.jacksoning.Jacksoning.LOGGER;
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
		LOGGER.log(Level.INFO, () -> "Setting role of " + this + " to " + role);
		roleState(role.newState());
	}

	public void setInitRole(PlayerRole role) {
		LOGGER.log(Level.INFO, () -> "Setting init role of " + this + " to " + role);
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

	public boolean isInsideNLR() {
		// TODO: get this from an nbt value on each NLF entity
		var radiusSq = MathHelper.square(16);
		var consumer = new LazyIterationConsumer<IronGolemEntity>() {
			private boolean found = false;

			@Override
			public NextIteration accept(IronGolemEntity entity) {
				if (player.squaredDistanceTo(entity) < radiusSq) {
					found = true;
					return NextIteration.ABORT;
				}
				return NextIteration.CONTINUE;
			}
		};
		forEachNLF(consumer);
		return consumer.found;
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

	public void onDisconnect() {
		if (game().state().isRunning()) {
			if (roleState().role() != PlayerRole.Jackson && (data().isSpawned() || roleState().role() == PlayerRole.UNLeader)) {
				onFatalDamage();
			}
		}
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
			if (data().isSpawned()) {
				keepModifiers.clear();
				switch (data().role()) {
					case Jackson -> {
						applyModifier(EntityAttributes.MAX_HEALTH,
									  MODIFIER_JACKSON_MAX_HEALTH,
									  4.0 * 24.0,
									  EntityAttributeModifier.Operation.ADD_VALUE);
						applyModifier(EntityAttributes.MOVEMENT_SPEED,
									  MODIFIER_JACKSON_SPEED,
									  0.02,
									  EntityAttributeModifier.Operation.ADD_VALUE);
					}
					case UNLeader -> {
						int unLeaderCount = 0;
						int mistressCount = 0;
						for (var p : game().players()) {
							switch (cast(p).roleState().role()) {
								case UNLeader -> unLeaderCount++;
								case Mistress -> mistressCount++;
								default -> {}
							}
						}
						if (unLeaderCount == 1) {
							applyModifier(EntityAttributes.MAX_HEALTH,
										  MODIFIER_PSY_MAX_HEALTH,
										  20.0 + 8.0 * mistressCount,
										  EntityAttributeModifier.Operation.ADD_VALUE);
						}
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
					shop.forEachLevel((item, lvl) -> item.onTick(this, lvl));
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
					if (!isNLFAlive()) {
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
						g.state().gameOver(server(), GameTeam.Jackson);
					} else {
						setRole(PlayerRole.Mistress);
						asMc().getInventory().dropAll();
					}
				}
				default -> {
				}
			}
		}

		respawnPlayer(g.state().inner.respawnCooldown());
	}

	private void forEachNLF(LazyIterationConsumer<IronGolemEntity> consumer) {
		((AccessorWorld) player.getWorld()).invokeGetEntityLookup()
										   .forEach(TypeFilter.instanceOf(IronGolemEntity.class),
													entity -> {
														if (entity.getCommandTags().contains("boss")) {
															return consumer.accept(entity);
														}
														return LazyIterationConsumer.NextIteration.CONTINUE;
													});
	}

	private boolean isNLFAlive() {
		var consumer = new LazyIterationConsumer<IronGolemEntity>() {
			private boolean found;

			@Override
			public NextIteration accept(IronGolemEntity entity) {
				found = true;
				return NextIteration.ABORT;
			}
		};
		forEachNLF(consumer);
		return consumer.found;
	}

	private void onJacksonFinalKill() {
		for (int i = 0; i < 20; i++) {
			LightningEntity entity = EntityType.LIGHTNING_BOLT.create(asMc().getWorld(), SpawnReason.EVENT);
			entity.refreshPositionAfterTeleport(asMc().getPos());
			entity.setCosmetic(true);
			asMc().getWorld().spawnEntity(entity);
		}

		// game over
		game().state().gameOver(server(), GameTeam.UN);
	}

	public void tpSpawn() {
		double x, y, z;
		var world = player.getServerWorld();
		if (game().state().isRunning() && roleState().role().team != null) {
			var playSpawnPoint = game().state().inner.playSpawnPoint();
			if (playSpawnPoint != null) {
				x = playSpawnPoint.getX();
				y = playSpawnPoint.getY();
				z = playSpawnPoint.getZ();
			} else {
				var spawnPos = world.getSpawnPos();
				x = spawnPos.getX();
				y = spawnPos.getY();
				z = spawnPos.getZ();
			}
		} else {
			var spawnPos = world.getSpawnPos();
			x = spawnPos.getX();
			y = spawnPos.getY();
			z = spawnPos.getZ();
		}
		asMc().teleport(world,
						x,
						y,
						z,
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
		tpSpawn();
		asMc().setHealth(asMc().getMaxHealth());
	}

	public void giveKit(int unLeaderCount) {
		var g = game();

		var inv = asMc().getInventory();
		inv.clear();
		switch (data().role()) {
			case UNLeader -> {
				inv.insertStack(new ItemStack(Items.EMERALD, g.state().inner.initialEmeraldsUN()));
			}
			case Jackson -> {
				inv.insertStack(new ItemStack(Items.EMERALD, g.state().inner.initialEmeraldsMJ() * unLeaderCount));
			}
			case Mistress -> {
			}
		}
	}
}
