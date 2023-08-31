package dev.pfaff.jacksoning.server;

import dev.pfaff.jacksoning.Config;
import dev.pfaff.jacksoning.PlayerRole;
import dev.pfaff.jacksoning.Winner;
import dev.pfaff.jacksoning.items.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

import static dev.pfaff.jacksoning.Config.GROOVE_INTERVAL;
import static dev.pfaff.jacksoning.Constants.MESSAGE_GAME_OVER_JACKSON_WON;
import static dev.pfaff.jacksoning.Constants.MESSAGE_GAME_OVER_UN_WON;

public final class GameState {
	static final long TIME_NOT_STARTED = -1;
	static final long TIME_ENDED = -2;

	//private static final List<Vec3i> ZONE_BEACON_OFFSETS = List.of(
	//	new Vec3i(-1, 0, -1),
	//	new Vec3i(1, 0, -1),
	//	new Vec3i(1, 0, 1),
	//	new Vec3i(-1, 0, 1)
	//);

	public final GameStateInner inner = new GameStateInner();

	public GameState() {
		inner.init();
	}

	/*
	 * state
	 */

	/**
	 * Whether the game has started. It might have ended!
	 */
	public boolean isStarted() {
		return lifecycle() != GameLifecycle.NotStarted;
	}

	/**
	 * Whether the game is ongoing.
	 */
	public boolean isRunning() {
		return inner.time() >= 0;
	}

	public long time() {
		return Math.max(inner.time(), 0);
	}

	public int grooveGifts() {
		return inner.grooveGifts();
	}

	public void grooveGifts(int grooveGifts) {
		inner.grooveGifts(grooveGifts);
	}

	public int economy() {
		return inner.economy();
	}

	public void boostEconomy(int addit) {
		inner.economy(inner.economy() + addit);
	}

	/**
	 * Whether the game has ended.
	 */
	public boolean isEnded() {
		return lifecycle() == GameLifecycle.Ended;
	}

	public GameLifecycle lifecycle() {
		if (inner.time() == TIME_NOT_STARTED) {
			return GameLifecycle.NotStarted;
		} else if (inner.time() == TIME_ENDED) {
			return GameLifecycle.Ended;
		} else {
			assert isRunning();
			return GameLifecycle.Running;
		}
	}

	public long timeUntilNextGroove() {
		return GROOVE_INTERVAL - (time() % GROOVE_INTERVAL);
	}

	/*
	 * lifecyle methods/actions
	 */

	public void start(MinecraftServer server) {
		if (isStarted()) throw new IllegalStateException("Game is already started" + (isEnded() ? " (and ended)" : ""));
		int jacksonCount = 0;
		int unLeaderCount = 0;
		for (var p : IGame.cast(server).players()) {
			switch (IGamePlayer.cast(p).state().role()) {
				case None -> {
				}
				case UNLeader -> unLeaderCount++;
				case Jackson -> jacksonCount++;
				case Mistress -> {
				}
				case Referee -> {
				}
			}
		}
		if (jacksonCount != 1) {
			Config.throwIncompleteCastError(server,
											"Expected the one and only Michael Jackson, but " + ((jacksonCount == 0)
																								 ? "he was nowhere to be found!"
																								 : "there were " + jacksonCount + " of him!"));
		}
		if (unLeaderCount == 0) {
			Config.throwIncompleteCastError(server, "Expected at least one UN leader!");
		}
		inner.time(0);
		assert isRunning();
		for (var p : IGame.cast(server).players()) {
			// TODO: https://git.pfaff.dev/michael/jacksoning/issues/8
			respawnPlayer(server, p, false);
		}

		//var spawnPos255 = server.getOverworld().getSpawnPos().withY(255);
		//var overworld = server.getOverworld();
		//for (var offset : ZONE_BEACON_OFFSETS) {
		//	var beaconPos = spawnPos255.add(offset);
		//	overworld.setBlockState(beaconPos, Blocks.END_GATEWAY.getDefaultState());
		//	((EndGatewayBlockEntity)overworld.getBlockEntity(beaconPos)).;
		//}
	}

	public void stop(MinecraftServer server) {
		if (!isRunning()) throw new IllegalStateException("Game is not running");
		inner.time(TIME_ENDED);
		assert isEnded();
		server.getPlayerManager().getPlayerList().forEach(p -> p.changeGameMode(GameMode.SPECTATOR));
	}

	public void reset(MinecraftServer server) {
		if (isRunning()) {
			stop(server);
		}
		inner.init();
	}

	public void tick(MinecraftServer server) {
		if (isRunning()) {
			long time = inner.time();
			// the number of groove gifts that should have been given by now
			// hopefully no one is able to cause ticks to skip while still increasing Jackson's economy
			// I'd rather give them extra groove than skip it entirely
			int targetGrooveGifts = (int) (time / GROOVE_INTERVAL);
			if (targetGrooveGifts != grooveGifts()) {
				int giftGroove = targetGrooveGifts - grooveGifts();

				server.getPlayerManager().getPlayerList().forEach(p -> {
					var role = IGamePlayer.cast(p).state().role();
					if (role == PlayerRole.Jackson || role == PlayerRole.Mistress) {
						p.giveItemStack(new ItemStack(Items.GROOVE, economy() * giftGroove));
					}
				});

				grooveGifts(targetGrooveGifts);
			}
			inner.time(time + 1);
		}
	}

	public void respawnPlayer(MinecraftServer server, ServerPlayerEntity player, boolean cooldown) {
		// TODO: https://git.pfaff.dev/michael/jacksoning/issues/4
		player.setOnFire(false);
		player.clearStatusEffects();
		player.setHealth(player.getMaxHealth());
		IGamePlayer.cast(player).state().applyGameMode(player);
		var spawnPos = server.getOverworld().getSpawnPos();
		player.teleport(server.getOverworld(), spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 0f, 0f);
	}

	public void gameOver(MinecraftServer server, Winner winner) {
		server.getPlayerManager().getPlayerList().forEach(p -> {
			p.sendMessageToClient(switch (winner) {
				case UN -> MESSAGE_GAME_OVER_UN_WON;
				case Jackson -> MESSAGE_GAME_OVER_JACKSON_WON;
			}, true);
		});
		this.stop(server);
		server.getPlayerManager().getPlayerList().forEach(p -> IGamePlayer.cast(p).setRole(PlayerRole.None));
	}
}
