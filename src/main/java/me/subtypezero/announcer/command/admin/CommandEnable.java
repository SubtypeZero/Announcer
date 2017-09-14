package me.subtypezero.announcer.command.admin;

import me.subtypezero.announcer.Announcer;
import me.subtypezero.announcer.command.CommandAnnounce;
import me.subtypezero.announcer.command.Permissions;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class CommandEnable implements CommandExecutor {
	private static final Announcer PLUGIN = Announcer.getInstance();
	public static final Text HELP_TEXT = Text.of("enable or disable announcements");

	private static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.ADMIN_PERM)
			.permission(Permissions.COMMAND_ENABLE)
			.description(HELP_TEXT)
			.arguments(GenericArguments.bool(Text.of("value")))
			.executor(new CommandEnable())
			.build();

	public static void register() {
		try {
			CommandAnnounce.addSubCommand(commandSpec, "enable");
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
			PLUGIN.getLogger().debug("Registered command: CommandEnable");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandEnable");
		}
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		boolean enable = args.<Boolean>getOne("value").get();
		PLUGIN.setAnnouncerEnabled(enable);
		if (enable) {
			src.sendMessage(Text.of(TextColors.GREEN, "Announcements enabled successfully!"));
		} else {
			src.sendMessage(Text.of(TextColors.GREEN, "Announcements disabled successfully!"));
		}
		return CommandResult.success();
	}
}