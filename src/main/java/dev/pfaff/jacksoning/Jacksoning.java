package dev.pfaff.jacksoning;

import com.mojang.logging.LogUtils;
import dev.pfaff.jacksoning.blocks.Blocks;
import dev.pfaff.jacksoning.data.Nicknames;
import dev.pfaff.jacksoning.entities.Entities;
import dev.pfaff.jacksoning.items.Items;
import dev.pfaff.jacksoning.packet.UpdateUIPacket;
import dev.pfaff.jacksoning.player.GamePlayer;
import dev.pfaff.jacksoning.server.Commands;
import dev.pfaff.jacksoning.util.LogDestupify;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.network.ServerPlayerEntity;

public class Jacksoning implements ModInitializer {
	public static final LogDestupify LOGGER = new LogDestupify(LogUtils.getLogger());

	@Override
	public void onInitialize() {
		Blocks.initialize();
		Items.initialize();
		Entities.initialize();

		PayloadTypeRegistry.playS2C().register(UpdateUIPacket.ID, UpdateUIPacket.CODEC);

		ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
			GamePlayer.cast(newPlayer).data.roleState = GamePlayer.cast(oldPlayer).data.roleState;
		});
		ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
			if (entity instanceof ServerPlayerEntity player) {
				GamePlayer.cast(player).onFatalDamage();
				return false;
			}
			return true;
		});
		ServerPlayConnectionEvents.INIT.register((handler, server) -> {
			GamePlayer.cast(handler.player).onConnect();
		});
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			GamePlayer.cast(handler.player).onDisconnect();
		});
		CommandRegistrationCallback.EVENT.register(Commands::register);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(Nicknames.INSTANCE);
	}
}
