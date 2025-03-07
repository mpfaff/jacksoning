package dev.pfaff.jacksoning.server;

import dev.pfaff.jacksoning.util.codec.Codecs;
import dev.pfaff.jacksoning.util.nbt.NbtCodecs;
import dev.pfaff.jacksoning.util.nbt.NbtElement;

import java.util.List;

/**
 * A team with a stake in the game. Separate from {@link McTeam} to provide some type-safety, for example when calling
 * {@link GameState#gameOver} (you wouldn't want the {@linkplain McTeam#Referee referees} to win, right?)
 */
public enum GameTeam {
	MJ(McTeam.MJ),
	UN(McTeam.UN);

	public static final List<GameTeam> VALUES = List.of(values());
	public static final dev.pfaff.jacksoning.util.codec.Codec<GameTeam, String> STRING_CODEC =
		Codecs.enumAsString(GameTeam.class, team -> team.mcTeam.id);
	public static final dev.pfaff.jacksoning.util.codec.Codec<GameTeam, NbtElement> NBT_CODEC =
		NbtCodecs.NBT_STRING.then(STRING_CODEC);

	public final McTeam mcTeam;

	private GameTeam(McTeam mcTeam) {
		this.mcTeam = mcTeam;
	}
}
