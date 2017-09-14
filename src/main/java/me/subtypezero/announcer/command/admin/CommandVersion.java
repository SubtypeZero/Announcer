package me.subtypezero.announcer.command.admin;

import me.subtypezero.announcer.Announcer;
import me.subtypezero.announcer.PluginInfo;
import me.subtypezero.announcer.command.CommandAnnounce;
import me.subtypezero.announcer.command.Permissions;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class CommandVersion implements CommandExecutor {
	private static final Announcer PLUGIN = Announcer.getInstance();
	public static final Text HELP_TEXT = Text.of("view plugin version information");

	private static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.ADMIN_PERM)
			.permission(Permissions.COMMAND_VERSION)
			.description(HELP_TEXT)
			.executor(new CommandVersion())
			.build();

	public static void register() {
		try {
			CommandAnnounce.addSubCommand(commandSpec, "version");
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
			PLUGIN.getLogger().debug("Registered command: CommandVersion");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandVersion");
		}
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		src.sendMessage(Text.of(TextColors.GREEN, "Version: ", PluginInfo.VERSION));
		return CommandResult.success();
	}
}