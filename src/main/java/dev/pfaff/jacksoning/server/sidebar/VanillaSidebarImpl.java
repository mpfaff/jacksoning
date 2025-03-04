package dev.pfaff.jacksoning.server.sidebar;

import dev.pfaff.jacksoning.server.IGame;
import dev.pfaff.jacksoning.sidebar.SidebarCommand;
import dev.pfaff.jacksoning.util.StringOrText;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreResetS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreUpdateS2CPacket;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.number.BlankNumberFormat;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;

import static dev.pfaff.jacksoning.util.Packets.scoreboardDisplayS2CPacket;
import static dev.pfaff.jacksoning.util.Packets.scoreboardObjectiveUpdateS2CPacket;

public final class VanillaSidebarImpl extends SidebarImpl {
	private static final String OBJECTIVE_NAME = "a1a996cf-2de1-4bad-b1df-887802f4dd01";

	private int length;
	private Text lastTitle;
	private boolean lastDevMode;

	@Override
	public boolean truncateIsLossy() {
		return true;
	}

	@Override
	public void initialize(ServerPlayerEntity p) {
		p.networkHandler.sendPacket(scoreboardObjectiveUpdateS2CPacket(
			p.getRegistryManager(),
			OBJECTIVE_NAME,
			false,
			Text.of(""),
			ScoreboardCriterion.RenderType.INTEGER,
			Optional.of(BlankNumberFormat.INSTANCE)
		));
		p.networkHandler.sendPacket(scoreboardDisplayS2CPacket(ScoreboardDisplaySlot.SIDEBAR, OBJECTIVE_NAME));
	}

	private static int mapIndex(int index) {
		return Integer.MAX_VALUE - index;
	}

	@Override
	public void sendUpdates(ServerPlayerEntity p, List<SidebarCommand> updates) {
		int newLength = -1;
		for (var command : updates) {
			switch (command) {
				case SidebarCommand.Truncate(var l) -> newLength = l;
				default -> {}
			}
		}

		int length = this.length;
		if (newLength != -1 && newLength != length) {
			if (newLength < length) {
				// remove excess entries
				for (int i = newLength; i < length; i++) {
					p.networkHandler.sendPacket(new ScoreboardScoreResetS2CPacket(
						Integer.toString(mapIndex(i)),
						OBJECTIVE_NAME
					));
				}
			} else {
				// add new entries
				for (int i = length; i < newLength; i++) {
					int index = mapIndex(i);
					p.networkHandler.sendPacket(new ScoreboardScoreUpdateS2CPacket(
						Integer.toString(index),
						OBJECTIVE_NAME,
						index,
						Optional.of(Text.empty()),
						Optional.empty()
					));
				}
			}

			this.length = length = newLength;
		}

		StringOrText titleUpdate = null;
		for (var command : updates) {
			// we don't support flags
			switch (command) {
				case SidebarCommand.SetLine(var index, var text, var flags) when index == 0 -> titleUpdate = text;
				case SidebarCommand.SetLine(var index, var text, var flags) -> {
					index = mapIndex(index);
					p.networkHandler.sendPacket(new ScoreboardScoreUpdateS2CPacket(
						Integer.toString(index),
						OBJECTIVE_NAME,
						index,
						Optional.of(text.asText()),
						Optional.empty()
					));
				}
				// handled earlier
				case SidebarCommand.Truncate(var l) -> {}
			}
		}

		boolean devMode = IGame.cast(p.server).state().devMode();
		if (titleUpdate != null || devMode != lastDevMode) {
			if (titleUpdate != null) {
				lastTitle = titleUpdate.asText();
			}
			lastDevMode = devMode;
			p.networkHandler.sendPacket(scoreboardObjectiveUpdateS2CPacket(
				p.getRegistryManager(),
				OBJECTIVE_NAME,
				true,
				lastTitle,
				ScoreboardCriterion.RenderType.INTEGER,
				devMode ? Optional.empty() : Optional.of(BlankNumberFormat.INSTANCE)
			));
		}
	}
}
