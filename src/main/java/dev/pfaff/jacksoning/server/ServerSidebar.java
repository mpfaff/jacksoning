package dev.pfaff.jacksoning.server;

import dev.pfaff.jacksoning.util.AndIntChangeNotifier;
import dev.pfaff.jacksoning.util.ChangeNotifier;
import dev.pfaff.jacksoning.PlayerRole;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import static dev.pfaff.jacksoning.Constants.LABEL_ROLE;
import static dev.pfaff.jacksoning.Constants.PACKET_UPDATE_UI;
import static dev.pfaff.jacksoning.SidebarCommand.setLine;
import static dev.pfaff.jacksoning.SidebarCommand.truncate;

public final class ServerSidebar {
	public final AndIntChangeNotifier<PlayerRole> roleChangeNotifier = AndIntChangeNotifier.identity();
	public final AndIntChangeNotifier<GameLifecycle> gameLifecycleChangeNotifier = AndIntChangeNotifier.identity();
	public final ChangeNotifier<BlockPos> blockPosChangeNotifier = ChangeNotifier.equality();
	// stupid long field names. I just need them to be unique per call-site.
	public final AndIntChangeNotifier<Boolean> insideJacksonZoneChangeNotifier = AndIntChangeNotifier.equality();
	public final AndIntChangeNotifier<Integer> economyChangeNotifier = AndIntChangeNotifier.equality();

	private int previousLength = 0;

	public void tick(ServerPlayerEntity p) {
		var gp = IGamePlayer.cast(p);
		var buf = PacketByteBufs.create();

		// write a placeholder line count
		truncate(69).writePacket(buf);

		// write the lines, counting as we go
		int i = 0;

		var gs = gp.game().state();
		var gameLifecycle = gs.lifecycle();

		if (gameLifecycleChangeNotifier.updateAndGet(gameLifecycle, i)) {
			setLine(i, switch (gameLifecycle) {
				case NotStarted -> "Awaiting Jackson";
				case Running -> "Rocking";
				case Ended -> "Concert Ended";
			}).writePacket(buf);
		}
		i++;

		if (gameLifecycle == GameLifecycle.Running) {
			if (gp.isReferee()) {
				setLine(i++, "Time: " + gs.time()).writePacket(buf);
			}

			if (gp.state().isSpawned()) {
				// TODO: only make copy when necessary.
				var blockPos = new BlockPos(p.getBlockPos());
				if (blockPosChangeNotifier.updateAndGet(blockPos)) {
					insideJacksonZoneChangeNotifier.updateA(gp.isInsideJacksonZone());
				}
				if (insideJacksonZoneChangeNotifier.updateBAndGet(i)) {
					boolean inside = insideJacksonZoneChangeNotifier.inputA();
					// TODO: only send when inside changes, not just when blockPos changes
					setLine(i, inside ? "Inside Neverland Ranch" : "Outside Neverland Ranch").writePacket(buf);
				}
				i++;

				switch (gp.state().role()) {
					case Jackson, Mistress -> {
						if (economyChangeNotifier.updateAndGet(gs.economy(), i)) {
							setLine(i, "Economy: " + gs.economy()).writePacket(buf);
						}
						i++;

						setLine(i++, "Groove drop in " + String.format("%.1f", gs.timeUntilNextGroove() / 20f) + "s").writePacket(buf);
					}
				}
			} else {
				setLine(i++, "Respawning in " + String.format("%.1f", gp.state().respawnTime / 20f) + "s").writePacket(buf);
			}
		}

		if (roleChangeNotifier.updateBAndGet(i)) {
			var role = gp.state().role();
			setLine(i, LABEL_ROLE.copy().append(Text.translatable(role.translationKey))).writePacket(buf);
		}
		i++;

		// set the real line count
		int writerIndex = buf.writerIndex();
		buf.writerIndex(0);
		truncate(i).writePacket(buf);
		if (i != previousLength) {
			previousLength = i;
		} else {
			buf.readerIndex(buf.writerIndex());
		}
		buf.writerIndex(writerIndex);

		ServerPlayNetworking.send(p, PACKET_UPDATE_UI, buf);
	}
}
