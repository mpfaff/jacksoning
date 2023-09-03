package dev.pfaff.jacksoning.server;

import com.mojang.logging.LogUtils;
import dev.pfaff.jacksoning.util.LogDestupify;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class JacksoningServer implements ModInitializer {
	public static final LogDestupify LOGGER = new LogDestupify(LogUtils.getLogger());

	@Override
	public void onInitialize() {
		ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
			GamePlayer.cast(newPlayer).roleState(GamePlayer.cast(oldPlayer).roleState());
		});
		ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
			if (entity instanceof ServerPlayerEntity player) {
				GamePlayer.cast(player).onFatalDamage();
				return false;
			}
			return true;
		});
		Commands.registerTypes();
		CommandRegistrationCallback.EVENT.register(Commands::register);
	}
}
