package dev.pfaff.jacksoning.server;

import dev.pfaff.jacksoning.util.codec.Codecs;
import dev.pfaff.jacksoning.util.nbt.NbtCodecs;
import dev.pfaff.jacksoning.util.nbt.NbtElement;

import static dev.pfaff.jacksoning.Constants.MOD_ID;

public enum GameTeam {
	Jackson("jackson"),
	UN("un");

	public static final dev.pfaff.jacksoning.util.codec.Codec<GameTeam, String> STRING_CODEC =
		Codecs.enumAsString(GameTeam.class, GameTeam::id);
	public static final dev.pfaff.jacksoning.util.codec.Codec<GameTeam, NbtElement> NBT_CODEC =
		NbtCodecs.NBT_STRING.then(STRING_CODEC);

	public final String id;
	public final String translationKey;

	private GameTeam(String id) {
		this.id = id;
		this.translationKey = "enum." + MOD_ID + ".team." + id;
	}

	public final String id() {
		return id;
	}
}
