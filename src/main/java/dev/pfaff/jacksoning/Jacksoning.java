package dev.pfaff.jacksoning;

import com.mojang.logging.LogUtils;
import dev.pfaff.jacksoning.blocks.Blocks;
import dev.pfaff.jacksoning.items.Items;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;

public class Jacksoning implements ModInitializer {
	public static final Logger LOGGER = LogUtils.getLogger();

	@Override
	public void onInitialize() {
		Blocks.register();
		Items.register();
	}
}
