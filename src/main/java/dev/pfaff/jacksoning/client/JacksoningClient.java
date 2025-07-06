package dev.pfaff.jacksoning.client;

import com.mojang.logging.LogUtils;
import dev.pfaff.jacksoning.packet.UpdateUIPacket;
import dev.pfaff.jacksoning.util.Logger;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import static dev.pfaff.jacksoning.Constants.MOD_ID;

public class JacksoningClient implements ClientModInitializer {
	public static final Logger LOGGER = new Logger(LogUtils.getLogger());

	private static final KeyBinding KEY_REFEREE_SCREEN = new KeyBinding("key.jacksoning.referee",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_R,
			"category.jacksoning");

	@Override
	public void onInitializeClient() {
		HudLayerRegistrationCallback.EVENT.register(drawer -> {
			drawer.addLayer(IdentifiedLayer.of(Identifier.of(MOD_ID, "sidebar"), ClientSidebar::render));
		});

		ClientPlayNetworking.registerGlobalReceiver(UpdateUIPacket.ID, (packet, context) -> {
			ClientSidebar.handleUpdate(packet);
		});

		KeyBindingHelper.registerKeyBinding(KEY_REFEREE_SCREEN);

		//ClientTickEvents.END_CLIENT_TICK.register(client -> {
		//	while (KEY_REFEREE_SCREEN.wasPressed()) {
		//		client.setScreen(RefereeScreen.create());
		//	}
		//});
	}
}
