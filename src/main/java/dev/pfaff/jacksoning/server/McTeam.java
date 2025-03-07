package dev.pfaff.jacksoning.server;

import java.util.List;

import static dev.pfaff.jacksoning.Constants.MOD_ID;

/**
 * The Minecraft teams that the mod may assign. The {@linkplain #mcTeam Minecraft team name} is temporarily overridden
 * for {@link #MJ} and {@link #UN} for compatibility reasons.
 *
 * dev.pfaff.jacksoning.PlayerRole to see which roles are assignment to which team.
 */
public enum McTeam {
	Spectator("spectator"),
	MJ("mj", "MJ"),
	UN("un", "UN"),
	Referee("referee");

	public static final List<McTeam> VALUES = List.of(values());

	public final String id;
	public final String translationKey;
	public final String mcTeam;
	public final String prefix;

	private McTeam(String id, String mcTeam) {
		this.id = id;
		this.translationKey = MOD_ID + ".team." + id;
		this.mcTeam = mcTeam;
		this.prefix = MOD_ID + ".team." + id + ".prefix";
	}

	private McTeam(String id) {
		this(id, id);
	}

	public final String id() {
		return id;
	}
}
