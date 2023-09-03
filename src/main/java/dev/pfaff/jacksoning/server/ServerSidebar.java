package dev.pfaff.jacksoning.server;

import dev.pfaff.jacksoning.Config;
import dev.pfaff.jacksoning.sidebar.Alignment;
import dev.pfaff.jacksoning.util.ChangeNotifier;
import dev.pfaff.jacksoning.util.memo.DiffingComputerList;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import static dev.pfaff.jacksoning.Constants.LABEL_ROLE;
import static dev.pfaff.jacksoning.Constants.GROOVE_VALUE_STYLE;
import static dev.pfaff.jacksoning.Constants.MESSAGE_GROOVE_DROP_NUMBER_STYLE;
import static dev.pfaff.jacksoning.Constants.MESSAGE_INSIDE_JACKSON_ZONE_FALSE;
import static dev.pfaff.jacksoning.Constants.MESSAGE_INSIDE_JACKSON_ZONE_TRUE;
import static dev.pfaff.jacksoning.Constants.PACKET_UPDATE_UI;
import static dev.pfaff.jacksoning.sidebar.SidebarCommand.setLine;
import static dev.pfaff.jacksoning.sidebar.SidebarCommand.truncate;

public final class ServerSidebar {
	public final ChangeNotifier<BlockPos> blockPosChangeNotifier = ChangeNotifier.equality();
	// stupid long field names. I just need them to be unique per call-site.
	public boolean insideJacksonZoneCached = false;

	private int previousLength = 0;
	private final DiffingComputerList<Void> lineDiffer = new DiffingComputerList<>();

	public void tick(ServerPlayerEntity p) {
		var gp = GamePlayer.cast(p);
		var buf = PacketByteBufs.create();

		// write a placeholder line count
		truncate(69).writePacket(buf);

		// write the lines, counting as we go
		int count = 0;

		var gs = gp.game().state();
		var gameLifecycle = gs.lifecycle();

		lineDiffer.get(count++, gameLifecycle, (a, ctx, i) -> {
			setLine(i, a.text, Alignment.Center).writePacket(ctx);
			return null;
		}, buf);

		if (gameLifecycle == GameLifecycle.Running) {
			if (gp.isReferee()) {
				lineDiffer.get(count++, gs.time(), (a, ctx, i) -> {
					setLine(i, "Time: " + a).writePacket(ctx);
					return null;
				}, buf);
			}

			if (gp.data().isSpawned()) {
				// TODO: only make copy when necessary.
				var blockPos = new BlockPos(p.getBlockPos());
				if (blockPosChangeNotifier.updateAndGet(blockPos)) {
					insideJacksonZoneCached = gp.isInsideJacksonZone();
				}
				lineDiffer.get(count++, insideJacksonZoneCached, (a, ctx, i) -> {
					setLine(i,
							a ? MESSAGE_INSIDE_JACKSON_ZONE_TRUE : MESSAGE_INSIDE_JACKSON_ZONE_FALSE).writePacket(ctx);
					return null;
				}, buf);

				switch (gp.data().role()) {
					case Jackson, Mistress -> {
						lineDiffer.get(count++, gs.economy(), (a, ctx, i) -> {
							setLine(i,
									Text.literal("Economy: ")
										.append(Text.literal(String.valueOf(a))
													.setStyle(GROOVE_VALUE_STYLE))).writePacket(ctx);
							return null;
						}, buf);

						lineDiffer.get(count++, gs.timeUntilNextGroove(), (a, ctx, i) -> {
							setLine(i,
									Text.literal("Groove drop in ")
										.append(Text.literal(String.format("%.1f", a / 20f) + "s")
													.setStyle(MESSAGE_GROOVE_DROP_NUMBER_STYLE))).writePacket(ctx);
							return null;
						}, buf);
					}
				}
			} else {
				lineDiffer.get(count++, gp.data().respawnTime, (a, ctx, i) -> {
					setLine(i, "Respawning in " + String.format("%.1f", a / 20f) + "s").writePacket(ctx);
					return null;
				}, buf);
			}
		}

		lineDiffer.get(count++, gp.data().role(), (a, ctx, i) -> {
			setLine(i, LABEL_ROLE.copy().append(Text.translatable(a.translationKey))).writePacket(ctx);
			return null;
		}, buf);

		if (Config.devMode()) {
			lineDiffer.get(count++, (ctx, i) -> {
				setLine(i, "Dev mode").writePacket(ctx);
				return null;
			}, buf);
		}

		// set the real line count
		int writerIndex = buf.writerIndex();
		buf.writerIndex(0);
		truncate(count).writePacket(buf);
		if (count != previousLength) {
			previousLength = count;
		} else {
			buf.readerIndex(buf.writerIndex());
		}
		buf.writerIndex(writerIndex);

		ServerPlayNetworking.send(p, PACKET_UPDATE_UI, buf);
	}
}
