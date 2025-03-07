package dev.pfaff.jacksoning.player;

import dev.pfaff.jacksoning.server.GameTeam;
import dev.pfaff.jacksoning.server.McTeam;
import dev.pfaff.jacksoning.server.RoleState;
import dev.pfaff.jacksoning.util.codec.Codecs;
import dev.pfaff.jacksoning.util.nbt.NbtCodecs;
import dev.pfaff.jacksoning.util.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;

import static dev.pfaff.jacksoning.Constants.MOD_ID;
import static dev.pfaff.jacksoning.util.codec.Codecs.enumByNameMap;
import static java.lang.invoke.MethodType.methodType;

public enum PlayerRole {
	None("none", McTeam.Spectator),
	UNLeader("un_leader", GameTeam.UN),
	Jackson("jackson", GameTeam.MJ),
	Mistress("mistress", GameTeam.MJ),
	Referee("referee", McTeam.Referee);

	public static final PlayerRole DEFAULT = None;

	public static final List<PlayerRole> VALUES = List.of(values());
	public static final Map<String, PlayerRole> BY_NAME = enumByNameMap(PlayerRole.class, PlayerRole::id);

	public static final dev.pfaff.jacksoning.util.codec.Codec<PlayerRole, String> STRING_CODEC =
		Codecs.enumAsString(PlayerRole.class, PlayerRole::id);
	public static final dev.pfaff.jacksoning.util.codec.Codec<PlayerRole, NbtElement> NBT_CODEC =
		NbtCodecs.NBT_STRING.then(STRING_CODEC);

	public final String id;
	public final String translationKey;
	@Nullable
	public final GameTeam team;
	public final McTeam mcTeam;
	private final MethodHandle stateConstructor;

	private PlayerRole(String id, @Nullable GameTeam team, McTeam mcTeam) {
		if (team != null) assert team.mcTeam == mcTeam;

		this.id = id;
		this.translationKey = MOD_ID + ".role." + id;
		this.team = team;
		this.mcTeam = mcTeam;
		var l = MethodHandles.lookup();
		try {
			this.stateConstructor = l.findConstructor(l.findClass(RoleState.class.getCanonicalName() + "$" + name()),
													  methodType(void.class)).asType(methodType(RoleState.class));
		} catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	private PlayerRole(String id, GameTeam team) {
		this(id, team, team.mcTeam);
	}

	private PlayerRole(String id, McTeam mcTeam) {
		this(id, null, mcTeam);
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
