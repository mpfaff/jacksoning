package dev.pfaff.jacksoning.server;

import dev.pfaff.jacksoning.PlayerRole;
import dev.pfaff.jacksoning.Winner;
import dev.pfaff.jacksoning.util.VecUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Objects;

import static dev.pfaff.jacksoning.Config.JACKSON_ZONE_RADIUS;

public interface IGamePlayer {
	public static IGamePlayer cast(ServerPlayerEntity player) {
		return (IGamePlayer) player;
	}

	public PlayerState state();
	public void setState(PlayerState state);

	public default void setRole(PlayerRole role) {
		JacksoningServer.LOGGER.info("Setting role of " + this + " to " + role);
		setState(role.newState());
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
		return this.state().role() == PlayerRole.Referee || ((ServerPlayerEntity)this).hasPermissionLevel(2);
	}

	public default boolean isInsideJacksonZone() {
		var spawnPos = server().getOverworld().getSpawnPos();
		return VecUtil.all(asMc().getBlockPos().subtract(spawnPos), dist -> Math.abs(dist) < JACKSON_ZONE_RADIUS);
	}

	public default void onFatalDamage() {
		var g = IGame.cast(server());

		switch (state().role()) {
			case Jackson -> {
				if (isInsideJacksonZone()) {
					for (int i = 0; i < 20; i++) {
						LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(asMc().world);
						lightningEntity.refreshPositionAfterTeleport(asMc().getPos());
						lightningEntity.setCosmetic(true);
					}

					// game over
					game().state().gameOver(server(), Winner.UN);
				}
			}
			case UNLeader -> {
				if (g.players().stream().filter(PlayerRole.UNLeader::matches).count() == 1) {
					game().state().gameOver(server(), Winner.Jackson);
				} else {
					setRole(PlayerRole.Mistress);
				}
			}
			default -> {
			}
		}

		game().state().respawnPlayer(server(), asMc(), true);
	}
}
