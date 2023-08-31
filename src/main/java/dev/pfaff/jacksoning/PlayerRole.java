package dev.pfaff.jacksoning;

import dev.pfaff.jacksoning.server.IGamePlayer;
import dev.pfaff.jacksoning.server.RoleState;
import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.world.GameMode;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

import static java.lang.invoke.MethodType.methodType;

public enum PlayerRole implements StringIdentifiable {
	None("none", GameMode.SPECTATOR),
	UNLeader("un_leader", GameMode.SURVIVAL),
	Jackson("jackson", GameMode.SURVIVAL),
	Mistress("mistress", GameMode.SURVIVAL),
	Referee("referee", GameMode.SPECTATOR);

	public static final PlayerRole DEFAULT = None;

	public final String id;
	public final String translationKey;
	public final GameMode gameMode;
	private final MethodHandle stateConstructor;

	public static final com.mojang.serialization.Codec<PlayerRole> CODEC = StringIdentifiable.createCodec(PlayerRole::values);
	public static final EnumArgumentType<PlayerRole> ARGUMENT_TYPE = new EnumArgumentType<>(CODEC,
																							PlayerRole::values) {};

	private PlayerRole(String id, GameMode gameMode) {
		this.id = id;
		this.translationKey = "enum.jacksoning." + id;
		this.gameMode = gameMode;
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

	@Override
	public final String asString() {
		return id;
	}

	public final boolean matches(ServerPlayerEntity p) {
		return IGamePlayer.cast(p).data().role() == this;
	}
}
