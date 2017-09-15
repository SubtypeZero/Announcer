package me.subtypezero.announcer.command;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.subtypezero.announcer.Announcer;
import me.subtypezero.announcer.PluginInfo;
import me.subtypezero.announcer.command.admin.CommandEnable;
import me.subtypezero.announcer.command.admin.CommandReload;
import me.subtypezero.announcer.command.admin.CommandVersion;
import me.subtypezero.announcer.command.mod.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.List;
import java.util.Map;

public class CommandAnnounce implements CommandExecutor {
	private static final Announcer PLUGIN = Announcer.getInstance();
	private static final String HELP_TEXT = String.format("used to run %s's sub-commands or display help info.", PluginInfo.NAME);

	private static final Map<List<String>, CommandCallable> children = Maps.newHashMap();

	public static void register() {
		registerSubCommands();

		CommandSpec commandSpec = CommandSpec.builder()
				.permission(Permissions.ADMIN_PERM)
				.permission(Permissions.MOD_PERM)
				.permission(Permissions.COMMAND_HELP)
				.description(Text.of(HELP_TEXT))
				.children(children)
				.arguments(GenericArguments.optionalWeak(GenericArguments.onlyOne(GenericArguments.literal(Text.of("help"), "help"))))
				.executor(new CommandAnnounce())
				.build();

		try {
			Sponge.getCommandManager().register(PLUGIN, commandSpec, "announce", "announcer", "acc");
			PLUGIN.getLogger().debug("Registered command: CommandAnnounce");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandAnnounce");
		}
	}

	private static void registerSubCommands() {
		CommandAdd.register();
		CommandRemove.register();
		CommandBroadcast.register();
		CommandSay.register();
		CommandInterval.register();
		CommandList.register();
		CommandRandom.register();
		CommandPrefix.register();
		CommandEnable.register();
		CommandReload.register();
		CommandVersion.register();
	}

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		List<Text> helpText = Lists.newArrayList();
		boolean hasPerms = false;
		boolean hasMod = false;
		boolean hasAdmin = false;

		if (src.hasPermission(Permissions.ADMIN_PERM)) {
			hasMod = true;
			hasAdmin = true;
		} else if (src.hasPermission(Permissions.MOD_PERM)) {
			hasMod = true;
		}

		if (hasMod || src.hasPermission(Permissions.COMMAND_ADD)) {
			helpText.add(Text.of(
					TextColors.AQUA, Text.builder("acc add").onClick(TextActions.suggestCommand("/acc add <message>")),
					TextColors.GOLD, " <message>",
					TextColors.DARK_GRAY, " - ",
					TextColors.DARK_GREEN, CommandAdd.HELP_TEXT
			));
			hasPerms = true;
		}


		if (hasMod || src.hasPermission(Permissions.COMMAND_REMOVE)) {
			helpText.add(Text.of(
					TextColors.AQUA, Text.builder("acc remove").onClick(TextActions.suggestCommand("/acc remove <index>")),
					TextColors.GOLD, " <index>",
					TextColors.DARK_GRAY, " - ",
					TextColors.DARK_GREEN, CommandRemove.HELP_TEXT
			));
			hasPerms = true;
		}

		if (hasMod || src.hasPermission(Permissions.COMMAND_BROADCAST)) {
			helpText.add(Text.of(
					TextColors.AQUA, Text.builder("acc broadcast").onClick(TextActions.suggestCommand("/acc broadcast <index>")),
					TextColors.GOLD, " <index>",
					TextColors.DARK_GRAY, " - ",
					TextColors.DARK_GREEN, CommandBroadcast.HELP_TEXT
			));
			hasPerms = true;
		}

		if (hasMod || src.hasPermission(Permissions.COMMAND_SAY)) {
			helpText.add(Text.of(
					TextColors.AQUA, Text.builder("acc say").onClick(TextActions.suggestCommand("/acc say <message>")),
					TextColors.GOLD, " <message>",
					TextColors.DARK_GRAY, " - ",
					TextColors.DARK_GREEN, CommandSay.HELP_TEXT
			));
			hasPerms = true;
		}

		if (hasMod || src.hasPermission(Permissions.COMMAND_INTERVAL)) {
			helpText.add(Text.of(
					TextColors.AQUA, Text.builder("acc interval").onClick(TextActions.suggestCommand("/acc interval <seconds>")),
					TextColors.GOLD, " <seconds>",
					TextColors.DARK_GRAY, " - ",
					TextColors.DARK_GREEN, CommandInterval.HELP_TEXT
			));
			hasPerms = true;
		}

		if (hasMod || src.hasPermission(Permissions.COMMAND_LIST)) {
			helpText.add(Text.of(
					TextColors.AQUA, Text.builder("acc list").onClick(TextActions.suggestCommand("/acc list")),
					TextColors.DARK_GRAY, " - ",
					TextColors.DARK_GREEN, CommandList.HELP_TEXT
			));
			hasPerms = true;
		}

		if (hasMod || src.hasPermission(Permissions.COMMAND_RANDOM)) {
			helpText.add(Text.of(
					TextColors.AQUA, Text.builder("acc mode").onClick(TextActions.suggestCommand("/acc mode <value>")),
					TextColors.GOLD, " <random|sequential>",
					TextColors.DARK_GRAY, " - ",
					TextColors.DARK_GREEN, CommandRandom.HELP_TEXT
			));
			hasPerms = true;
		}

		if (hasMod || src.hasPermission(Permissions.COMMAND_PREFIX)) {
			helpText.add(Text.of(
					TextColors.AQUA, Text.builder("acc prefix").onClick(TextActions.suggestCommand("/acc prefix <value>")),
					TextColors.GOLD, " <value>",
					TextColors.DARK_GRAY, " - ",
					TextColors.DARK_GREEN, CommandPrefix.HELP_TEXT
			));
			hasPerms = true;
		}

		if (hasAdmin || src.hasPermission(Permissions.COMMAND_ENABLE)) {
			helpText.add(Text.of(
					TextColors.AQUA, Text.builder("acc enable").onClick(TextActions.suggestCommand("/acc enable false")),
					TextColors.GRAY, " [true|false]",
					TextColors.DARK_GRAY, " - ",
					TextColors.DARK_GREEN, CommandEnable.HELP_TEXT
			));
			hasPerms = true;
		}

		if (hasAdmin || src.hasPermission(Permissions.COMMAND_RELOAD)) {
			helpText.add(Text.of(
					TextColors.AQUA, Text.builder("acc reload").onClick(TextActions.suggestCommand("/acc reload")),
					TextColors.DARK_GRAY, " - ",
					TextColors.DARK_GREEN, CommandReload.HELP_TEXT
			));
			hasPerms = true;
		}

		if (hasAdmin || src.hasPermission(Permissions.COMMAND_VERSION)) {
			helpText.add(Text.of(
					TextColors.AQUA, Text.builder("acc version").onClick(TextActions.suggestCommand("/acc version")),
					TextColors.DARK_GRAY, " - ",
					TextColors.DARK_GREEN, CommandVersion.HELP_TEXT
			));
			hasPerms = true;
		}

		if (hasPerms) {
			PaginationList.builder()
					.title(Text.of(TextColors.AQUA, PluginInfo.NAME, " Help"))
					.padding(Text.of(TextColors.AQUA, TextStyles.STRIKETHROUGH, "-"))
					.contents(helpText)
					.sendTo(src);
		} else {
			src.sendMessage(Text.of(PluginInfo.NAME + " " + PluginInfo.VERSION));
		}

		return CommandResult.success();
	}

	public static void addSubCommand(CommandCallable child, String... aliases) {
		children.put(ImmutableList.copyOf(aliases), child);
	}

	public static void clearSubCommands() {
		children.clear();
	}
}
