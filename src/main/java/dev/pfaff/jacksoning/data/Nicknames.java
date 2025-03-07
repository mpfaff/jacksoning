package dev.pfaff.jacksoning.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dev.pfaff.jacksoning.player.PlayerRole;
import dev.pfaff.jacksoning.player.GamePlayer;
import dev.pfaff.jacksoning.server.GameState;
import dev.pfaff.jacksoning.server.IGame;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static dev.pfaff.jacksoning.Constants.MOD_ID;

public final class Nicknames implements IdentifiableResourceReloadListener {
	public static final Nicknames INSTANCE = new Nicknames();
	public static final Gson GSON = new Gson();
	public static final TypeToken<List<String>> LIST_STRING_TYPE =
		(TypeToken<List<String>>) TypeToken.getParameterized(List.class, String.class);

	private final EnumMap<PlayerRole, List<String>> nicknames = new EnumMap<>(PlayerRole.class);

	private Nicknames() {
		for (var role : PlayerRole.VALUES) {
			nicknames.put(role, List.of());
		}
	}

	@Nullable
	public String getNickname(GameState gameState, PlayerRole role, UUID player) {
		var list = nicknames.get(role);
		if (list.isEmpty()) return null;
		var index = new Random(gameState.inner.seed() + player.hashCode()).nextInt(list.size());
		return list.get(index);
	}

	@Nullable
	public String getNickname(ServerPlayerEntity player) {
		return getNickname(IGame.cast(player.server).state(), GamePlayer.cast(player).roleState().role(), player.getUuid());
	}

	@Override
	public Identifier getFabricId() {
		return Identifier.of(MOD_ID, "nicknames");
	}

	@Override
	public CompletableFuture<Void> reload(Synchronizer synchronizer,
										  ResourceManager manager,
										  Executor prepareExecutor,
										  Executor applyExecutor) {
		return CompletableFuture.supplyAsync(() -> {
			return PlayerRole.VALUES.stream().map(role -> {
				var res = manager.getResource(Identifier.of(MOD_ID, "nicknames/" + role.id + ".json"));
				if (res.isEmpty()) return List.<String>of();
				try (var rdr = res.get().getReader()) {
					return List.copyOf(GSON.fromJson(rdr, LIST_STRING_TYPE));
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			}).toList();
		}, prepareExecutor).thenComposeAsync(synchronizer::whenPrepared).thenAcceptAsync(nicknames -> {
			for (int i = 0; i < nicknames.size(); i++) {
				this.nicknames.put(PlayerRole.VALUES.get(i), nicknames.get(i));
			}
		}, applyExecutor);
	}
}
