package dev.pfaff.jacksoning;

import com.mojang.logging.LogUtils;
import dev.pfaff.jacksoning.blocks.Blocks;
import dev.pfaff.jacksoning.items.Items;
import dev.pfaff.jacksoning.packet.UpdateUIPacket;
import dev.pfaff.jacksoning.util.LogDestupify;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class Jacksoning implements ModInitializer {
	public static final LogDestupify LOGGER = new LogDestupify(LogUtils.getLogger());

	@Override
	public void onInitialize() {
		Blocks.initialize();
		Items.initialize();

		PayloadTypeRegistry.playS2C().register(UpdateUIPacket.ID, UpdateUIPacket.CODEC);
	}
}
