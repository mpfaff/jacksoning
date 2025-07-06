package dev.pfaff.jacksoning.client;

import com.mojang.logging.LogUtils;
import dev.pfaff.jacksoning.packet.UpdateUIPacket;
import dev.pfaff.jacksoning.util.Logger;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.util.Identifier;

import static dev.pfaff.jacksoning.Constants.MOD_ID;

public class JacksoningClient implements ClientModInitializer {
	public static final Logger LOGGER = new Logger(LogUtils.getLogger());

	@Override
	public void onInitializeClient() {
		HudLayerRegistrationCallback.EVENT.register(drawer -> {
			drawer.addLayer(IdentifiedLayer.of(Identifier.of(MOD_ID, "sidebar"), ClientSidebar::render));
		});

		ClientPlayNetworking.registerGlobalReceiver(UpdateUIPacket.ID, (packet, context) -> {
			ClientSidebar.handleUpdate(packet);
		});
	}
}
