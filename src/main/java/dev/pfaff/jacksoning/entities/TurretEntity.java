package dev.pfaff.jacksoning.entities;

import com.mojang.datafixers.util.Pair;
import dev.pfaff.jacksoning.server.GameTeam;
import dev.pfaff.jacksoning.util.Angle2;
import dev.pfaff.jacksoning.util.codec.Codec;
import dev.pfaff.jacksoning.util.codec.CodecException;
import dev.pfaff.jacksoning.util.nbt.ContainerCodecHelper;
import dev.pfaff.jacksoning.util.nbt.MinecraftNbtWrapper;
import dev.pfaff.jacksoning.util.nbt.NbtElement;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.slf4j.event.Level;
import xyz.nucleoid.packettweaker.PacketContext;

import java.lang.invoke.MethodHandles;
import java.util.List;

import static dev.pfaff.jacksoning.Constants.MOD_ID;
import static dev.pfaff.jacksoning.Jacksoning.LOGGER;
import static dev.pfaff.jacksoning.util.nbt.ContainerCodecHelper.containerField;
import static dev.pfaff.jacksoning.util.nbt.NbtCodecs.NBT_COMPOUND;
import static dev.pfaff.jacksoning.util.nbt.NbtCodecs.NBT_FLOAT;
import static dev.pfaff.jacksoning.util.nbt.NbtCodecs.NBT_INT;
import static java.util.Comparator.comparing;

// TODO: extend LivingEntity instead
public final class TurretEntity extends MobEntity implements PolymerEntity, IGameEntity {
	private static final Identifier MODIFIER_ATTACK_DAMAGE = Identifier.of(MOD_ID, "attack_damage");
	private static final Identifier MODIFIER_DEFENCE = Identifier.of(MOD_ID, "defence");

	private static final int ACTIVATION_TIME = 22;
	// copied from bow time (1.1s)
	private static final int COOLDOWN_TIME = 22;
	private static final int DEACTIVATION_TIME = 20 * 2;

	private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

	private static Codec<ItemStack, NbtElement> uncountedItemStackCodec(DynamicRegistryManager registryManager) {
		return NBT_COMPOUND.then(Codec.by(stack -> {
			var ops = registryManager.getOps(NbtOps.INSTANCE);
			var compound = new NbtCompound();
			ItemStack.UNCOUNTED_CODEC.encode(stack, ops, compound).getOrThrow(CodecException::new);
			return NbtElement.of(compound);
		}, nbt -> {
			var ops = registryManager.getOps(NbtOps.INSTANCE);
			return ItemStack.UNCOUNTED_CODEC.parse(ops, ((MinecraftNbtWrapper) nbt).nbt()).getOrThrow(CodecException::new);
		}));
	}

	public static final ContainerCodecHelper<TurretEntity> CODEC = ContainerCodecHelper.componentBy(MOD_ID, List.of(
		containerField(LOOKUP, "damage", NBT_FLOAT.or(2f)),
		containerField(LOOKUP, "armor", NBT_FLOAT.or(0f)),
		containerField(LOOKUP, "range", NBT_FLOAT.or(16f)),
		containerField(LOOKUP, "team", GameTeam.NBT_CODEC.or(GameTeam.UN)),
		// TODO: storage that does not depend on the configurable values
		containerField(LOOKUP, "cooldown", NBT_INT.or(ACTIVATION_TIME)),
		containerField(LOOKUP, "idleTicks", NBT_INT.or(DEACTIVATION_TIME))
	));

	private GameTeam team;
	private float damage;
	private float armor;
	private float range;
	private int cooldown;
	private int idleTicks;

	public TurretEntity(EntityType<? extends MobEntity> entityType, World world) {
		super(entityType, world);
		this.lookControl = new UnmanagedLookControl(this);
	}

	public static DefaultAttributeContainer.Builder defaultAttributesBuilder() {
		return createLivingAttributes()
			.add(EntityAttributes.KNOCKBACK_RESISTANCE, 1.0)
			.add(EntityAttributes.ATTACK_DAMAGE, 0.0)
			// required by MobEntity
			.add(EntityAttributes.FOLLOW_RANGE, 0.0);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		try {
			CODEC.write(this, NbtElement.of(nbt));
		} catch (CodecException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		try {
			CODEC.read(NbtElement.of(nbt), this);
		} catch (CodecException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public GameTeam gameTeam() {
		return team;
	}

	@Override
	protected Text getDefaultName() {
		return Text.translatable("entity." + MOD_ID + ".turret", Text.translatable(team.translationKey));
	}

	@Override
	public EntityType<?> getPolymerEntityType(PacketContext packetContext) {
		return EntityType.SKELETON;
	}

	@Override
	public List<Pair<EquipmentSlot, ItemStack>> getPolymerVisibleEquipment(List<Pair<EquipmentSlot, ItemStack>> items,
																		   ServerPlayerEntity player) {
		return List.of(
			Pair.of(EquipmentSlot.MAINHAND, Items.BOW.getDefaultStack()),
			Pair.of(EquipmentSlot.HEAD, Items.END_ROD.getDefaultStack()),
			Pair.of(EquipmentSlot.CHEST, Items.LEATHER_CHESTPLATE.getDefaultStack()),
			Pair.of(EquipmentSlot.LEGS, Items.LEATHER_LEGGINGS.getDefaultStack()),
			Pair.of(EquipmentSlot.FEET, Items.LEATHER_BOOTS.getDefaultStack())
		);
	}

	private void updateAttributes() {
		this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).updateModifier(new EntityAttributeModifier(
			MODIFIER_ATTACK_DAMAGE,
			damage,
			EntityAttributeModifier.Operation.ADD_VALUE
		));
		this.getAttributeInstance(EntityAttributes.ARMOR).updateModifier(new EntityAttributeModifier(
			MODIFIER_DEFENCE,
			armor,
			EntityAttributeModifier.Operation.ADD_VALUE
		));
	}

	private Angle2 calculateLookAtEntity(Entity targetEntity, float maxYawChange, float maxPitchChange) {
		double d = targetEntity.getX() - this.getX();
		double e = targetEntity.getZ() - this.getZ();
		double f;
		if (targetEntity instanceof LivingEntity livingEntity) {
			f = livingEntity.getEyeY() - this.getEyeY();
		} else {
			f = (targetEntity.getBoundingBox().minY + targetEntity.getBoundingBox().maxY) / 2.0 - this.getEyeY();
		}

		double g = Math.sqrt(d * d + e * e);
		float h = (float)(MathHelper.atan2(e, d) * 180.0F / (float)Math.PI) - 90.0F;
		float i = (float)(-(MathHelper.atan2(f, g) * 180.0F / (float)Math.PI));
		return new Angle2(
			lookControl.changeAngle(this.getYaw(), h, maxYawChange),
			lookControl.changeAngle(this.getPitch(), i, maxPitchChange)
		);
	}

	private boolean isValidTarget(LivingEntity entity) {
		if (entity == this) return false;
		if (this.isTeammate(entity)) return false;
		if (!entity.isAlive()) return false;
		if (entity.isInvulnerable()) return false;
		return true;
	}

	private Angle2 getTargetLook(LivingEntity entity) {
		Vec3d vec3d = this.getCameraPosVec(1f);
		var look = this.calculateLookAtEntity(entity, 180f, 90f);
		Vec3d vec3d2 = this.getRotationVector(look.pitch(), look.yaw());
		double range = this.range;
		Vec3d vec3d3 = vec3d.add(vec3d2.x * range, vec3d2.y * range, vec3d2.z * range);

		// find blocks in our way
		var result = this.getWorld().raycast(new RaycastContext(
			vec3d,
			vec3d3,
			RaycastContext.ShapeType.COLLIDER,
			RaycastContext.FluidHandling.NONE,
			this
		));
		double rangeSq = MathHelper.square(range);
		if (result.getType() != HitResult.Type.MISS) {
			double f = result.getPos().squaredDistanceTo(vec3d);
			if (f < rangeSq) {
				rangeSq = f;
				range = Math.sqrt(f);
			}
		}

		vec3d3 = vec3d.add(vec3d2.x * range, vec3d2.y * range, vec3d2.z * range);
		Box box = getBoundingBox().stretch(vec3d2.multiply(range)).expand(1.0, 1.0, 1.0);
		// ProjectileUtil.raycast maxDistance is squared
		EntityHitResult entityHitResult = ProjectileUtil.raycast(this, vec3d, vec3d3, box, EntityPredicates.CAN_HIT, rangeSq);
		if (entityHitResult == null) return null;
		var dist = entityHitResult.getPos().squaredDistanceTo(vec3d);
		if (dist >= rangeSq) {
			LOGGER.log(Level.WARN, (a, b) -> "Unexpected raycast result: distance exceeds range: " + a + " >= " + b, Math.sqrt(dist), range);
		}
		if (entityHitResult.getEntity() != entity) return null;
		return look;
	}

	private void tickAi(ServerWorld world) {
		var profiler = Profilers.get();
		profiler.push("turret ai");
		try {
			var targets = world.getEntitiesByClass(LivingEntity.class,
												   Box.of(getPos(), range * 2, range * 2, range * 2),
												   this::isValidTarget);

			if (!targets.isEmpty()) {
				idleTicks = 0;

				targets.sort(comparing(entity -> entity.distanceTo(this)));

				LivingEntity target = null;
				Angle2 look = null;
				for (var entity : targets) {
					look = getTargetLook(entity);
					if (look != null) {
						target = entity;
						break;
					}
				}

				if (target != null) {
					this.setPitch(look.pitch());
					this.setHeadYaw(look.yaw());

					if (cooldown <= 0) {
						cooldown = COOLDOWN_TIME;

						tryAttack(world, target);
					}
				}

				if (cooldown > 0) {
					cooldown--;
				}
			} else {
				if (idleTicks >= DEACTIVATION_TIME) {
					this.setPitch(80f);
					cooldown = ACTIVATION_TIME;
				} else {
					idleTicks++;
					// keep ticking down cooldown while not yet deactivated if no entities in range
					if (cooldown > 0) {
						cooldown--;
					}
				}
			}
		} finally {
			profiler.pop();
		}
	}

	@Override
	protected void mobTick(ServerWorld world) {
		super.mobTick(world);

		this.updateAttributes();

		if (!this.isAiDisabled()) tickAi(world);
	}

	private static final class UnmanagedLookControl extends LookControl {
		public UnmanagedLookControl(MobEntity entity) {
			super(entity);
		}

		@Override
		public void tick() {
		}
	}
}
