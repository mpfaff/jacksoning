package dev.pfaff.jacksoning;

import dev.pfaff.jacksoning.player.GamePlayer;
import dev.pfaff.jacksoning.server.GameTeam;
import dev.pfaff.jacksoning.server.RoleState;
import dev.pfaff.jacksoning.util.codec.Codecs;
import dev.pfaff.jacksoning.util.nbt.NbtCodecs;
import dev.pfaff.jacksoning.util.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Map;

import static dev.pfaff.jacksoning.Constants.MOD_ID;
import static dev.pfaff.jacksoning.util.codec.Codecs.enumByNameMap;
import static java.lang.invoke.MethodType.methodType;

public enum PlayerRole {
	None("none", GameMode.SPECTATOR, null),
	UNLeader("un_leader", GameMode.SURVIVAL, GameTeam.UN),
	Jackson("jackson", GameMode.SURVIVAL, GameTeam.Jackson),
	Mistress("mistress", GameMode.SURVIVAL, GameTeam.Jackson),
	Referee("referee", GameMode.SPECTATOR, null);

	public static final PlayerRole DEFAULT = None;

	public static final dev.pfaff.jacksoning.util.codec.Codec<PlayerRole, String> STRING_CODEC =
		Codecs.enumAsString(PlayerRole.class, PlayerRole::id);
	public static final dev.pfaff.jacksoning.util.codec.Codec<PlayerRole, NbtElement> NBT_CODEC =
		NbtCodecs.NBT_STRING.then(STRING_CODEC);

	public static final Map<String, PlayerRole> BY_NAME = enumByNameMap(PlayerRole.class, PlayerRole::id);

	public final String id;
	public final String translationKey;
	public final String decoratedNameTranslationKey;
	public final GameMode gameMode;
	public final GameTeam team;
	public final String mcTeam;
	private final MethodHandle stateConstructor;

	private PlayerRole(String id, GameMode gameMode, GameTeam team) {
		this.id = id;
		this.translationKey = "enum." + MOD_ID + ".role." + id;
		this.decoratedNameTranslationKey = MOD_ID + ".role.decorated_name." + id;
		this.gameMode = gameMode;
		this.team = team;
		// temporarily override UN role for compatibility with the map
		this.mcTeam = id.equals("un_leader") ? "UN" : id;
		var l = MethodHandles.lookup();
		try {
			this.stateConstructor = l.findConstructor(l.findClass(RoleState.class.getCanonicalName() + "$" + name()),
													  methodType(void.class)).asType(methodType(RoleState.class));
		} catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	public final RoleState newState() {
		try {
			return (RoleState) stateConstructor.invokeExact();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public final String id() {
		return id;
	}

	public final boolean matches(ServerPlayerEntity p) {
		return GamePlayer.cast(p).data().role() == this;
	}
}
