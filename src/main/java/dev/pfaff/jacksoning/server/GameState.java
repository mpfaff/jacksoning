package dev.pfaff.jacksoning.server;

import dev.pfaff.jacksoning.player.PlayerRole;
import dev.pfaff.jacksoning.player.GamePlayer;
import net.minecraft.registry.Registries;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

import java.util.List;

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

	public boolean devMode() {
		return inner.devMode();
	}

	public void devMode(boolean enable) {
		inner.devMode(enable);
	}

	public boolean allowIncompleteCast() {
		return devMode();
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

	/*
	 * lifecyle methods/actions
	 */

	public void start(MinecraftServer server) {
		if (isStarted()) throw new IllegalStateException("Game is already started" + (isEnded() ? " (and ended)" : ""));
		int jacksonCount = 0;
		int unLeaderCount = 0;
		for (var p : IGame.cast(server).players()) {
			var gp = GamePlayer.cast(p);

			// reset the role state for the new game
			gp.setRole(gp.data().role());

			switch (gp.data().role()) {
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
			throwIncompleteCastError(server,
									 "Expected the one and only Michael Jackson, but " + ((jacksonCount == 0)
																						  ? "he was nowhere to be found!"
																						  : "there were " + jacksonCount + " of him!"));
		}
		if (unLeaderCount == 0) {
			throwIncompleteCastError(server, "Expected at least one UN leader!");
		}
		inner.time(0);
		assert isRunning();
		server.getOverworld().setTimeOfDay(8000);
		for (var p : IGame.cast(server).players()) {
			var gp = GamePlayer.cast(p);
			gp.respawnPlayer(gp.data().role() == PlayerRole.Jackson ? inner.spawnDelayMJ() : 0);
			gp.giveKit(unLeaderCount);
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
		resetPlayers(server);
	}

	/**
	 * Resets ephemeral server state.
	 */
	private void resetServerState(MinecraftServer server) {
		var sb = server.getScoreboard();
		List.copyOf(sb.getTeams()).forEach(team -> {
			switch (team.getName()) {
				// temporarily leave these two for compatibility with the map
				case "UN", "MJ" -> {}
				default -> sb.removeTeam(team);
			}
		});
		for (var team : McTeam.VALUES) {
			var mcTeam = sb.addTeam(team.mcTeam);
			mcTeam.setDisplayName(Text.translatable(team.translationKey));
			mcTeam.setColor(switch (team) {
				case UN -> Formatting.BLUE;
				case MJ -> Formatting.BLACK;
				case Referee -> Formatting.RED;
				case Spectator -> Formatting.GRAY;
			});
			mcTeam.setPrefix(Text.translatable(team.prefix));
			// this should be the default, but apparently not. Goofy.
			mcTeam.setNameTagVisibilityRule(AbstractTeam.VisibilityRule.NEVER);
		}
	}

	private void resetPlayers(MinecraftServer server) {
		server.getPlayerManager().getPlayerList().forEach(p -> {
			p.getInventory().clear();
			Registries.ATTRIBUTE.streamEntries().forEach(attribute -> {
				var inst = p.getAttributeInstance(attribute);
				if (inst == null) return;
				for (var modifier : inst.getModifiers()) inst.removeModifier(modifier.id());
			});
			p.changeGameMode(GameMode.SPECTATOR);
			GamePlayer.cast(p).tpSpawn();
		});
	}

	public void reset(MinecraftServer server) {
		if (isRunning()) {
			stop(server);
		} else {
			resetPlayers(server);
		}
		resetServerState(server);
		inner.init();
	}

	public void init(MinecraftServer server) {
		resetServerState(server);
	}

	public void tick(MinecraftServer server) {
		if (isRunning()) {
			long time = inner.time();
			if (inner.jacksonLastSeen() != -1L && inner.jacksonLastSeen() + inner.jacksonTimeout() <= time) {
				gameOver(server, GameTeam.UN);
			}
			for (var p : server.getPlayerManager().getPlayerList()) {
				if (GamePlayer.cast(p).roleState().role() == PlayerRole.Jackson) {
					inner.jacksonLastSeen(time);
					break;
				}
			}
			if (isRunning()) {
				inner.time(time + 1);
			}
		}
	}

	public void gameOver(MinecraftServer server, GameTeam winner) {
		server.getPlayerManager().getPlayerList().forEach(p -> {
			p.sendMessageToClient(switch (winner) {
				case UN -> MESSAGE_GAME_OVER_UN_WON;
				case MJ -> MESSAGE_GAME_OVER_JACKSON_WON;
			}, true);
		});
		this.stop(server);
		server.getPlayerManager().getPlayerList().forEach(p -> {
			var gp = GamePlayer.cast(p);
			switch (gp.data().role()) {
				case Mistress -> gp.setRole(PlayerRole.UNLeader);
				default -> {
				}
			}
		});
	}

	private void throwIncompleteCastError(MinecraftServer server, String message) {
		if (allowIncompleteCast()) {
			server.getPlayerManager().getPlayerList().forEach(p -> {
				p.sendMessage(Text.literal(message).styled(s -> s.withColor(Formatting.YELLOW)));
			});
		} else {
			throw new IllegalStateException(message);
		}
	}
}
