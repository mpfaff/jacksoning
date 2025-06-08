package dev.pfaff.jacksoning.server;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.pfaff.jacksoning.player.PlayerRole;
import dev.pfaff.jacksoning.player.GamePlayer;
import dev.pfaff.jacksoning.server.shop.PurchaseResult;
import dev.pfaff.jacksoning.server.shop.ShopScreenHandler;
import dev.pfaff.jacksoning.server.shop.ShopState;
import dev.pfaff.jacksoning.util.codec.CodecException;
import dev.pfaff.jacksoning.util.nbt.ContainerCodecHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.slf4j.event.Level;

import java.util.Objects;

import static dev.pfaff.jacksoning.Constants.MOD_ID;
import static dev.pfaff.jacksoning.Jacksoning.LOGGER;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class Commands {
	public static boolean isReferee(ServerCommandSource source) {
		if (source.hasPermissionLevel(2)) return true;
		var player = source.getPlayer();
		if (player == null) return false;
		return GamePlayer.cast(player).isReferee();
	}

	private static Command<ServerCommandSource> catchingIllegalState(Command<ServerCommandSource> f) {
		return context -> {
			try {
				return f.run(context);
			} catch (IllegalStateException e) {
				context.getSource().sendError(Text.of(e.getMessage()));
				return 1;
			} catch (CommandSyntaxException e) {
				throw e;
			} catch (Throwable e) {
				LOGGER.log(Level.ERROR, "'/" + context.getInput() + "' threw an exception", e);
				if (IGame.cast(context.getSource().getServer()).state().devMode()) {
					context.getSource().sendError(Text.translatable("command.failed"));
					context.getSource().sendError(Text.of(e.toString()));
					return 1;
				} else {
					throw e;
				}
			}
		};
	}

	private static void sendShop(CommandContext<ServerCommandSource> context, ShopState shop) {
		var src = context.getSource();
		src.sendMessage(Text.literal("Shop:"));
		shop.forEachLevel((item, lvl) -> {
			var msg = item.id() + "[lvl=" + lvl + "] ";
			if (item.isMaxLevel(lvl)) {
				msg += "Sold out";
			} else {
				msg += item.name(lvl+1) + ": " + item.cost(lvl+1) + " groove";
			}
			src.sendMessage(Text.literal(msg));
		});
	}

	private static ShopState getShop(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		var gp = GamePlayer.cast(Objects.requireNonNull(context.getSource().getPlayer()));
		if (!gp.game().state().isRunning()) throw new SimpleCommandExceptionType(Text.translatable("message.jacksoning.shop.not_running")).create();
		if (!gp.data().isSpawned()) throw new SimpleCommandExceptionType(Text.translatable("message.jacksoning.shop.not_spawned")).create();
		var shop = gp.roleState().shop();
		if (shop == null) throw new SimpleCommandExceptionType(Text.translatable("message.jacksoning.shop.cannot_use")).create();
		return shop;
	}

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
								CommandRegistryAccess registryAccess,
								CommandManager.RegistrationEnvironment environment) {
		var configCommand = literal("config")
			.then(literal("reset").executes(context -> {
				try {
					IGame.cast(context.getSource().getServer()).state().inner.resetConfig();
				} catch (CodecException e) {
					context.getSource().sendError(Text.of(e.toString()));
					return 1;
				}
				return 0;
			}));
		for (var field : GameStateInner.FIELDS) {
			var configProps = field.configProps();
			if (configProps == null) continue;
			configCommand = configCommand.then(
				literal(field.field())
					.executes(catchingIllegalState(context -> {
						var g = IGame.cast(context.getSource().getServer());
						Object value;
						try {
							value = field.containerField().getter().get(g.state().inner);
						} catch (CodecException e) {
							context.getSource().sendError(Text.of(e.toString()));
							return 1;
						}
						context.getSource().sendMessage(Text.of(field.field() + ": " + value));
						return 0;
					}))
					.then(argument("value", configProps.argumentType()).executes(catchingIllegalState(context -> {
						Object value = configProps.get(context);
						var g = IGame.cast(context.getSource().getServer());
						try {
							((ContainerCodecHelper.FieldSetter<GameStateInner, Object>) field.containerField().setter()).set(g.state().inner, value);
						} catch (CodecException e) {
							context.getSource().sendError(Text.of(e.toString()));
							return 1;
						}
						g.state().inner.persistentState.markDirty();
						context.getSource().sendMessage(Text.of(field.field() + ": " + value));
						return 0;
					})))
			);
		}
		dispatcher.register(literal(MOD_ID).then(literal("start").executes(catchingIllegalState(context -> {
			var state = IGame.cast(context.getSource().getServer()).state();
			if (state.isEnded()) {
				state.reset(context.getSource().getServer());
			}
			state.start(context.getSource().getServer());
			return 0;
		}))).then(literal("stop").executes(catchingIllegalState(context -> {
			IGame.cast(context.getSource().getServer()).state().stop(context.getSource().getServer());
			return 0;
		}))).then(literal("reset").executes(catchingIllegalState(context -> {
			IGame.cast(context.getSource().getServer()).state().reset(context.getSource().getServer());
			return 0;
		}))).then(configCommand));
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
					return new ShopScreenHandler(syncId, inv, shop);
				}
			});
			return 0;
		})).then(literal("list").executes(catchingIllegalState(context -> {
			var shop = getShop(context);
			sendShop(context, shop);
			return 0;
		}))).then(literal("buy").then(argument("item", StringArgumentType.word()).suggests((context, builder) -> {
			var shop = getShop(context);
			for (var item : shop.shop().items) {
				builder.suggest(item.id());
			}
			return builder.buildFuture();
		}).executes(context -> {
			var shop = getShop(context);
			var gp = GamePlayer.cast(Objects.requireNonNull(context.getSource().getPlayer()));
			var result = shop.purchase(gp, StringArgumentType.getString(context, "item"));
			if (result != PurchaseResult.Success) {
				context.getSource().sendError(result.text);
				return 1;
			}
			return 0;
		}))));
		dispatcher.register(
			literal("setrole")
				.requires(Commands::isReferee)
				.then(
					argument("players", EntityArgumentType.players())
						.then(
							argument(
								"role",
								StringArgumentType.word()
							).suggests((context, builder1) -> {
								for (var role : PlayerRole.VALUES) {
									builder1.suggest(role.id);
								}
								return builder1.buildFuture();
							}).executes(context -> {
								var players = EntityArgumentType.getPlayers(
									context,
									"players"
								);
								var roleString = context.getArgument("role", String.class);
								PlayerRole role = PlayerRole.BY_NAME.get(roleString);
								if (role == null) {
									throw new SimpleCommandExceptionType(Text.translatable("argument." + MOD_ID + ".role.invalid", roleString)).create();
								}
								for (var player : players) {
									var gp = GamePlayer.cast(player);
									gp.setInitRole(role);
								}
								return 0;
							})
						)
				)
		);
	}
}
