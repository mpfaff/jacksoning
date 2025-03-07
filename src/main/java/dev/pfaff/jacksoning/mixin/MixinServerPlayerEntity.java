package dev.pfaff.jacksoning.mixin;

import dev.pfaff.jacksoning.player.GamePlayer;
import dev.pfaff.jacksoning.player.IGamePlayer;
import dev.pfaff.jacksoning.server.GameTeam;
import dev.pfaff.jacksoning.util.codec.CodecException;
import dev.pfaff.jacksoning.util.nbt.NbtElement;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.pfaff.jacksoning.player.PlayerData.PLAYER_NBT_KEY;
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

	@Override
	public GameTeam gameTeam() {
		return IGamePlayer.super.gameTeam();
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

	@Inject(method = "getPlayerListName", at = @At("HEAD"), cancellable = true)
	private final void getPlayerListName$nickname(CallbackInfoReturnable<Text> cir) {
		var nickname = gamePlayer.nickname();
		if (nickname != null) {
			var realName = ((ServerPlayerEntity) (Object) this).getGameProfile().getName();
			cir.setReturnValue(Text.of(nickname + " (" + realName + ")"));
		}
	}
}
