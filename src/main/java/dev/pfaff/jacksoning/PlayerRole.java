package dev.pfaff.jacksoning;

import dev.pfaff.jacksoning.server.IGamePlayer;
import dev.pfaff.jacksoning.server.PlayerState;
import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.StringIdentifiable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.function.Predicate;

import static java.lang.invoke.MethodType.methodType;

public enum PlayerRole implements StringIdentifiable {
	None("none"), UNLeader("un_leader"), Jackson("jackson"), Mistress("mistress"), Referee("referee");

	public static final PlayerRole DEFAULT = None;

	public final String id;
	public final String translationKey;
	private final MethodHandle stateConstructor;

	public static final com.mojang.serialization.Codec<PlayerRole> CODEC = StringIdentifiable.createCodec(PlayerRole::values);
	public static final EnumArgumentType<PlayerRole> ARGUMENT_TYPE = new EnumArgumentType<>(CODEC,
																							PlayerRole::values) {};

	private PlayerRole(String id) {
		this.id = id;
		this.translationKey = "enum.jacksoning." + id;
		var l = MethodHandles.lookup();
		try {
			this.stateConstructor = l.findConstructor(l.findClass(PlayerState.class.getCanonicalName() + "$" + name()),
													  methodType(void.class)).asType(methodType(PlayerState.class));
		} catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	public final PlayerState newState() {
		try {
			return (PlayerState) stateConstructor.invokeExact();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public final String asString() {
		return id;
	}

	public final boolean matches(ServerPlayerEntity p) {
		return IGamePlayer.cast(p)
				   .state()
				   .role() == this;
	}
}
