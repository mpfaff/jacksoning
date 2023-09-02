package dev.pfaff.jacksoning.client;

import com.mojang.logging.LogUtils;
import dev.pfaff.jacksoning.util.LogDestupify;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import static dev.pfaff.jacksoning.Constants.PACKET_UPDATE_UI;

public class JacksoningClient implements ClientModInitializer {
	public static final LogDestupify LOGGER = new LogDestupify(LogUtils.getLogger());

	@Override
	public void onInitializeClient() {
		HudRenderCallback.EVENT.register(ClientSidebar::render);

		// TODO: make sure these are handled in order
		ClientPlayNetworking.registerGlobalReceiver(PACKET_UPDATE_UI, (client, handler, buf, responseSender) -> {
			ClientSidebar.handleUpdate(buf);
		});
	}
}
