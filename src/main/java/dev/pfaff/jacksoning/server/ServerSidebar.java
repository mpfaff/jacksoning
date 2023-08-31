package dev.pfaff.jacksoning.server;

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
	public final ChangeNotifier<PlayerRole> roleChangeNotifier = ChangeNotifier.identity();
	public final ChangeNotifier<Integer> roleLineNumberChangeNotifier = ChangeNotifier.equality();
	public final ChangeNotifier<GameLifecycle> gameLifecycleChangeNotifier = ChangeNotifier.identity();
	public final ChangeNotifier<Integer> gameLifecycleLineNumberChangeNotifier = ChangeNotifier.equality();
	public final ChangeNotifier<BlockPos> blockPosChangeNotifier = ChangeNotifier.equality();
	public final ChangeNotifier<Integer> insideJacksonZoneLineNumberChangeNotifier = ChangeNotifier.equality();

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

		if (gameLifecycleChangeNotifier.updateAndGet(gameLifecycle) || gameLifecycleLineNumberChangeNotifier.updateAndGet(i)) {
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

			// TODO: only make copy when necessary.
			var blockPos = new BlockPos(p.getBlockPos());
			if (blockPosChangeNotifier.updateAndGet(blockPos) || insideJacksonZoneLineNumberChangeNotifier.updateAndGet(i)) {
				var inside = gp.isInsideJacksonZone();
				// TODO: only send when inside changes, not just when blockPos changes
				setLine(i, inside ? "Inside Neverland Ranch" : "Outside Neverland Ranch").writePacket(buf);
			}
			i++;

			switch (gp.state().role()) {
				case Jackson, Mistress -> {
					setLine(i++, "Groove drop in " + String.format("%.1f", gs.timeUntilNextGroove() / 20f) + "s").writePacket(buf);
				}
			}
		}

		if (roleChangeNotifier.get() || roleLineNumberChangeNotifier.updateAndGet(i)) {
			var role = gp.state().role();
			setLine(i++, LABEL_ROLE.copy().append(Text.translatable(role.translationKey))).writePacket(buf);
		} else {
			// keep the line.
			i++;
		}

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
