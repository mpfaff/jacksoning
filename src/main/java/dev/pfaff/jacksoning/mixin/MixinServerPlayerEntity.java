package dev.pfaff.jacksoning.mixin;

import dev.pfaff.jacksoning.server.GamePlayer;
import dev.pfaff.jacksoning.server.IGamePlayer;
import dev.pfaff.jacksoning.util.codec.CodecException;
import dev.pfaff.jacksoning.util.nbt.NbtElement;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.pfaff.jacksoning.server.PlayerData.PLAYER_NBT_KEY;
import static dev.pfaff.jacksoning.util.nbt.NbtCodecs.NBT_COMPOUND;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity implements IGamePlayer {
	@Shadow
	@Final
	public MinecraftServer server;

	@Unique
	public final GamePlayer gamePlayer = new GamePlayer((ServerPlayerEntity) (Object) this);

	@Override
	public GamePlayer gamePlayer() {
		return gamePlayer;
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private final void tick(CallbackInfo ci) {
		gamePlayer.tick();
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
	private final void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
		var nbtWrapper = NbtElement.of(nbt);
		try {
			var inner = nbtWrapper.getAs(PLAYER_NBT_KEY, NBT_COMPOUND.or(null));
			if (inner != null) {
				gamePlayer.data.readNbt(inner);
				// trigger change notifiers
				gamePlayer.roleState(gamePlayer.data.roleState);
			}
		} catch (CodecException e) {
			throw new RuntimeException(e);
		}
	}

	@Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
	private final void writeCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
		var nbtWrapper = NbtElement.of(nbt);
		nbtWrapper.put(PLAYER_NBT_KEY, gamePlayer.data.writeNbt());
	}
}
