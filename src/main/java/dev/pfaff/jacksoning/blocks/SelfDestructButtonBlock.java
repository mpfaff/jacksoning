package dev.pfaff.jacksoning.blocks;

import dev.pfaff.jacksoning.server.IGame;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StoneButtonBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

import java.util.List;
import java.util.Optional;

import static dev.pfaff.jacksoning.Config.economyBoostPerOutput;

public final class SelfDestructButtonBlock extends StoneButtonBlock {
	private static final BooleanProperty TRIGGERING = BooleanProperty.of("triggering");

	private static final ExplosionBehavior EXPLOSION_BEHAVIOR = new ExplosionBehavior() {
		@Override
		public Optional<Float> getBlastResistance(Explosion explosion,
												  BlockView world,
												  BlockPos pos,
												  BlockState blockState,
												  FluidState fluidState) {
			var resistance = super.getBlastResistance(explosion, world, pos, blockState, fluidState);
			return resistance.map(f -> f / 5f);
		}
	};
	private static final List<Vec3i> OFFSETS = List.of(
		new Vec3i(-1, 0, 0),
		new Vec3i(0, 0, -1),
		new Vec3i(1, 0, 0),
		new Vec3i(0, 0, 1)
	);

	public SelfDestructButtonBlock(Settings settings) {
		super(settings);
		this.setDefaultState(getDefaultState().with(TRIGGERING, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(TRIGGERING);
	}

	@Override
	public void powerOn(BlockState state, World world, BlockPos pos) {
		if (!(world instanceof ServerWorld serverWorld)) return;
		if (!IGame.cast(serverWorld.getServer()).state().isRunning()) return;
		world.setBlockState(pos, state.with(POWERED, true), Block.NOTIFY_ALL);
		world.createAndScheduleBlockTick(pos, this, 20);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (state.get(POWERED)) {
			world.setBlockState(pos, state.with(POWERED, false).with(TRIGGERING, true), Block.NOTIFY_ALL);
			world.createAndScheduleBlockTick(pos, this, 20 * 3);

			world.spawnParticles(new DustParticleEffect(new Vec3f(92 / 255f, 0f, 0f), 4f), pos.getX(), pos.getY() + 0.5, pos.getZ(), 512, 4, 4, 4, 0.4);
		} else if (state.get(TRIGGERING)) {
			world.breakBlock(pos, false);
			IGame.cast(world.getServer()).state().boostEconomy(economyBoostPerOutput());
			for (int y = 1; y >= -3; y--) {
				for (var offset : OFFSETS) {
					var explosionPos = pos.add(offset.multiply(6)).add(0, y * 4, 0);
					world.createExplosion(null,
										  null,
										  EXPLOSION_BEHAVIOR,
										  explosionPos.getX(),
										  explosionPos.getY() + 0.5,
										  explosionPos.getZ(),
										  4.0f,
										  true,
										  Explosion.DestructionType.DESTROY);
				}
			}
		}
	}

	/**
	 * Don't let the UN leaders do something sneaky and remove the button by removing its mount. The button will stay there.
	 */
	@Override
	public BlockState getStateForNeighborUpdate(BlockState state,
												Direction direction,
												BlockState neighborState,
												WorldAccess world,
												BlockPos pos,
												BlockPos neighborPos) {
		return state;
	}
}
