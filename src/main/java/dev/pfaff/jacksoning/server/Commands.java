package dev.pfaff.jacksoning.server;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.pfaff.jacksoning.PlayerRole;
import dev.pfaff.jacksoning.server.shop.Shop;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

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

	private static void sendShop(CommandContext<ServerCommandSource> context, Shop shop) {
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
		})))).then(literal("shop").requires(ServerCommandSource::isExecutedByPlayer).then(literal("list").executes(catchingIllegalState(context -> {
			var gp = IGamePlayer.cast(context.getSource().getPlayer());
			var shop = switch (gp.roleState()) {
				case RoleState.None ignored -> null;
				case RoleState.UNLeader ignored -> null;
				case RoleState.Referee ignored -> null;
				case RoleState.Jackson jackson -> jackson.shop;
				case RoleState.Mistress mistress -> mistress.shop;
			};
			if (shop == null) {
				context.getSource().sendError(Text.of("You cannot access the shop"));
				return 1;
			}
			sendShop(context, shop);
			return 0;
		})))));
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
