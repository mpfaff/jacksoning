package dev.pfaff.jacksoning.util;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.packet.s2c.play.ScoreboardDisplayS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.NumberFormatTypes;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Optional;

/// The helpers in this class must be kept in sync with their respective packets' formats.
public final class Packets {
	private static final MethodHandle MH_newScoreboardDisplayS2CPacket;
	private static final MethodHandle MH_newScoreboardObjectiveUpdateS2CPacket;

	public static ScoreboardDisplayS2CPacket scoreboardDisplayS2CPacket(ScoreboardDisplaySlot slot, String objective) {
		var buf = PacketByteBufs.create();
		buf.encode(ScoreboardDisplaySlot::getId, slot);
		buf.writeString(objective);
		try {
			return (ScoreboardDisplayS2CPacket) MH_newScoreboardDisplayS2CPacket.invokeExact(buf);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static ScoreboardObjectiveUpdateS2CPacket scoreboardObjectiveUpdateS2CPacket(
		DynamicRegistryManager registryManager,
		String name,
		boolean update,
		Text displayName,
		ScoreboardCriterion.RenderType type,
		Optional<NumberFormat> numberFormat
	) {
		var buf = new RegistryByteBuf(PacketByteBufs.create(), registryManager);
		buf.writeString(name);
		buf.writeByte(update ? ScoreboardObjectiveUpdateS2CPacket.UPDATE_MODE : ScoreboardObjectiveUpdateS2CPacket.ADD_MODE);
		TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.encode(buf, displayName);
		buf.writeEnumConstant(type);
		NumberFormatTypes.OPTIONAL_PACKET_CODEC.encode(buf, numberFormat);
		try {
			return (ScoreboardObjectiveUpdateS2CPacket) MH_newScoreboardObjectiveUpdateS2CPacket.invokeExact(buf);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static ScoreboardObjectiveUpdateS2CPacket scoreboardObjectiveUpdateS2CPacketRemove(DynamicRegistryManager registryManager, String name) {
		var buf = new RegistryByteBuf(PacketByteBufs.create(), registryManager);
		buf.writeString(name);
		buf.writeByte(ScoreboardObjectiveUpdateS2CPacket.REMOVE_MODE);
		try {
			return (ScoreboardObjectiveUpdateS2CPacket) MH_newScoreboardObjectiveUpdateS2CPacket.invokeExact(buf);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	static {
		try {
			var l = MethodHandles.lookup();
			MH_newScoreboardDisplayS2CPacket = MethodHandles.privateLookupIn(ScoreboardDisplayS2CPacket.class, MethodHandles.lookup())
															.findConstructor(
																ScoreboardDisplayS2CPacket.class,
																MethodType.methodType(void.class, PacketByteBuf.class)
															);
			MH_newScoreboardObjectiveUpdateS2CPacket = MethodHandles.privateLookupIn(ScoreboardObjectiveUpdateS2CPacket.class, MethodHandles.lookup())
																	.findConstructor(
																		ScoreboardObjectiveUpdateS2CPacket.class,
																		MethodType.methodType(void.class, RegistryByteBuf.class)
																	);
		} catch (NoSuchMethodException | IllegalAccessException e) {
			throw new ExceptionInInitializerError(e);
		}
	}
}
