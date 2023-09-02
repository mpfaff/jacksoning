package dev.pfaff.jacksoning.server;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.pfaff.jacksoning.PlayerRole;
import dev.pfaff.jacksoning.server.shop.PurchaseResult;
import dev.pfaff.jacksoning.server.shop.ShopState;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Objects;

import static dev.pfaff.jacksoning.Constants.TYPE_PLAYER_ROLE;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class Commands {
	public static boolean isReferee(ServerCommandSource source) {
		if (source.hasPermissionLevel(2)) return true;
		var player = source.getPlayer();
		if (player == null) return false;
		return IGamePlayer.cast(player).isReferee();
	}

	public static void registerTypes() {
		ArgumentTypeRegistry.registerArgumentType(TYPE_PLAYER_ROLE,
												  PlayerRole.ARGUMENT_TYPE.getClass(),
												  ConstantArgumentSerializer.of(() -> PlayerRole.ARGUMENT_TYPE));
	}

	private static Command<ServerCommandSource> catchingIllegalState(Command<ServerCommandSource> f) {
		return context -> {
			try {
				return f.run(context);
			} catch (IllegalStateException e) {
				context.getSource().sendError(Text.of(e.getMessage()));
				return 1;
			}
		};
	}

	private static void sendShop(CommandContext<ServerCommandSource> context, ShopState shop) {
		var src = context.getSource();
		src.sendMessage(Text.literal("Shop:"));
		shop.levels().forEach(entry -> {
			var item = entry.getKey();
			var lvl = entry.getIntValue();
			var msg = item.id() + "[level=" + lvl + "] ";
			if (item.isUpgrade() && lvl >= item.maxLevel()) {
				msg += "Sold out";
			} else {
				msg += item.name(lvl+1) + ": " + item.cost(lvl+1) + " groove";
			}
			src.sendMessage(Text.literal(msg));
		});
	}

	private static ShopState getShop(CommandContext<ServerCommandSource> context) {
		var gp = IGamePlayer.cast(Objects.requireNonNull(context.getSource().getPlayer()));
		if (!gp.game().state().isRunning()) throw new CommandException(Text.translatable("message.jacksoning.shop_not_running"));
		if (!gp.data().isSpawned()) throw new CommandException(Text.translatable("message.jacksoning.shop_not_spawned"));
		var shop = gp.roleState().shop();
		if (shop == null) throw new CommandException(Text.translatable("message.jacksoning.shop_cannot_use"));
		return shop;
	}

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
								CommandRegistryAccess registryAccess,
								CommandManager.RegistrationEnvironment environment) {
		dispatcher.register(literal("jacksoning").then(literal("start").executes(catchingIllegalState(context -> {
			IGame.cast(context.getSource().getServer()).state().start(context.getSource().getServer());
			return 0;
		}))).then(literal("stop").executes(catchingIllegalState(context -> {
			IGame.cast(context.getSource().getServer()).state().stop(context.getSource().getServer());
			return 0;
		}))).then(literal("reset").executes(catchingIllegalState(context -> {
			IGame.cast(context.getSource().getServer()).state().reset(context.getSource().getServer());
			return 0;
		}))).then(literal("restart").executes(catchingIllegalState(context -> {
			var state = IGame.cast(context.getSource().getServer()).state();
			state.reset(context.getSource().getServer());
			state.start(context.getSource().getServer());
			return 0;
		}))).then(literal("boostEconomy").then(argument("addit", IntegerArgumentType.integer(0)).executes(catchingIllegalState(context -> {
			int addit = IntegerArgumentType.getInteger(context, "addit");
			IGame.cast(context.getSource().getServer()).state().boostEconomy(addit);
			return 0;
		})))).then(literal("devMode").then(argument("enable", BoolArgumentType.bool()).executes(catchingIllegalState(context -> {
			boolean enable = BoolArgumentType.getBool(context, "enable");
			IGame.cast(context.getSource().getServer()).state().devMode(enable);
			return 0;
		})))));
		dispatcher.register(literal("shop").requires(ServerCommandSource::isExecutedByPlayer).executes(catchingIllegalState(context -> {
			var shop = getShop(context);
			var p = Objects.requireNonNull(context.getSource().getPlayer());
			p.openHandledScreen(new NamedScreenHandlerFactory() {
				@Override
				public Text getDisplayName() {
					return Text.of("Groove Shop");
				}

				@Override
				public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
					return GenericContainerScreenHandler.createGeneric9x3(syncId, inv);
				}
			});
			sendShop(context, shop);
			return 0;
		})).then(literal("list").executes(catchingIllegalState(context -> {
			var shop = getShop(context);
			sendShop(context, shop);
			return 0;
		}))).then(literal("buy").then(argument("item", StringArgumentType.word()).suggests((context, builder) -> {
			var shop = getShop(context);
			for (var id : shop.items().keySet()) {
				builder.suggest(id);
			}
			return builder.buildFuture();
		}).executes(context -> {
			var shop = getShop(context);
			var gp = IGamePlayer.cast(Objects.requireNonNull(context.getSource().getPlayer()));
			var result = shop.purchase(gp, StringArgumentType.getString(context, "item"));
			if (result != PurchaseResult.Success) {
				context.getSource().sendError(result.text);
				return 1;
			}
			return 0;
		}))));
		dispatcher.register(literal("setrole").requires(Commands::isReferee)
											  .then(argument("players", EntityArgumentType.players()).then(argument(
												  "role",
												  PlayerRole.ARGUMENT_TYPE).executes(context -> {
												  var players = EntityArgumentType.getPlayers(context, "players");
												  var role = context.getArgument("role", PlayerRole.class);
												  for (var player : players) {
													  var gp = IGamePlayer.cast(player);
													  gp.setInitRole(role);
												  }
												  return 0;
											  }))));
	}
}
