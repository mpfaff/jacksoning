package dev.pfaff.jacksoning.server.sidebar;

import dev.pfaff.jacksoning.packet.UpdateUIPacket;
import dev.pfaff.jacksoning.server.GameLifecycle;
import dev.pfaff.jacksoning.player.GamePlayer;
import dev.pfaff.jacksoning.server.IGame;
import dev.pfaff.jacksoning.sidebar.Alignment;
import dev.pfaff.jacksoning.sidebar.SidebarCommand;
import dev.pfaff.jacksoning.util.ChangeNotifier;
import dev.pfaff.jacksoning.util.memo.DiffingComputerList;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.slf4j.event.Level;

import java.util.ArrayList;
import java.util.List;

import static dev.pfaff.jacksoning.Constants.GROOVE_VALUE_STYLE;
import static dev.pfaff.jacksoning.Constants.LABEL_ROLE;
import static dev.pfaff.jacksoning.Constants.MESSAGE_GROOVE_DROP_NUMBER_STYLE;
import static dev.pfaff.jacksoning.Constants.MESSAGE_INSIDE_JACKSON_ZONE_FALSE;
import static dev.pfaff.jacksoning.Constants.MESSAGE_INSIDE_JACKSON_ZONE_TRUE;
import static dev.pfaff.jacksoning.Jacksoning.LOGGER;
import static dev.pfaff.jacksoning.sidebar.SidebarCommand.setLine;
import static dev.pfaff.jacksoning.sidebar.SidebarCommand.truncate;

public final class ServerSidebar {
	private SidebarImpl impl;

	public final ChangeNotifier<BlockPos> blockPosChangeNotifier = ChangeNotifier.equality();
	public boolean insideJacksonZoneCached = false;

	private int previousLength = 0;
	private final DiffingComputerList lineDiffer = new DiffingComputerList();

	public void initialize(ServerPlayerEntity player) {
		impl = ServerPlayNetworking.canSend(player, UpdateUIPacket.ID)
			   ? new CustomSidebarImpl()
			   : new VanillaSidebarImpl();
		impl.initialize(player);
	}

	public void tick(ServerPlayerEntity p) {
		var gp = GamePlayer.cast(p);
		List<SidebarCommand> buf = new ArrayList<>();

		// insert a placeholder for the line count
		buf.add(null);

		// write the lines, counting as we go
		int count = 0;

		var gs = gp.game().state();
		var gameLifecycle = gs.lifecycle();

		lineDiffer.get(count++, gameLifecycle, (a, ctx, i) -> {
			ctx.add(setLine(i, a.text, Alignment.Middle));
			return null;
		}, buf);

		if (gameLifecycle == GameLifecycle.Running) {
			if (gp.isReferee()) {
				lineDiffer.get(count++, gs.time(), (a, ctx, i) -> {
					ctx.add(setLine(i, "Time: " + a));
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
					ctx.add(setLine(i,
							a ? MESSAGE_INSIDE_JACKSON_ZONE_TRUE : MESSAGE_INSIDE_JACKSON_ZONE_FALSE));
					return null;
				}, buf);

				switch (gp.data().role()) {
					case Jackson, Mistress -> {
						lineDiffer.get(count++, gs.economy(), (a, ctx, i) -> {
							ctx.add(setLine(i,
									Text.literal("Economy: ")
										.append(Text.literal(String.valueOf(a))
													.setStyle(GROOVE_VALUE_STYLE))));
							return null;
						}, buf);

						lineDiffer.get(count++, gs.timeUntilNextGroove(), (a, ctx, i) -> {
							ctx.add(setLine(i,
									Text.literal("Groove drop in ")
										.append(Text.literal(String.format("%.1f", a / 20f) + "s")
													.setStyle(MESSAGE_GROOVE_DROP_NUMBER_STYLE))));
							return null;
						}, buf);
					}
				}
			} else {
				lineDiffer.get(count++, gp.data().respawnTime, (a, ctx, i) -> {
					ctx.add(setLine(i, "Respawning in " + String.format("%.1f", a / 20f) + "s"));
					return null;
				}, buf);
			}
		}

		lineDiffer.get(count++, gp.data().role(), (a, ctx, i) -> {
			ctx.add(setLine(i, LABEL_ROLE.copy().append(Text.translatable(a.translationKey))));
			return null;
		}, buf);

		if (IGame.cast(p.server).state().devMode()) {
			lineDiffer.get(count++, (ctx, i) -> {
				ctx.add(setLine(i, "Dev mode"));
				return null;
			}, buf);
		}

		// set the real line count
		if (count != previousLength) {
			if (impl.truncateIsLossy()) {
				lineDiffer.truncate(count);
			}
			buf.set(0, truncate(count));
			previousLength = count;
		} else {
			buf = buf.subList(1, buf.size());
		}

		if (!buf.isEmpty()) {
			LOGGER.log(Level.INFO, n -> "Sending " + n + " sidebar updates", buf.size());

			impl.sendUpdates(p, buf);
		}
	}
}
