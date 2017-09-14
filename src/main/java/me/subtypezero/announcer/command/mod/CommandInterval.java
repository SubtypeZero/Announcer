package me.subtypezero.announcer.command.mod;

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

public class CommandInterval implements CommandExecutor {
	private static final Announcer PLUGIN = Announcer.getInstance();
	public static final Text HELP_TEXT = Text.of("set the announcement interval");

	private static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.ADMIN_PERM)
			.permission(Permissions.MOD_PERM)
			.permission(Permissions.COMMAND_INTERVAL)
			.description(HELP_TEXT)
			.arguments(GenericArguments.integer(Text.of("seconds")))
			.executor(new CommandInterval())
			.build();

	public static void register() {
		try {
			CommandAnnounce.addSubCommand(commandSpec, "interval");
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
			PLUGIN.getLogger().debug("Registered command: CommandInterval");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandInterval");
		}
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		int seconds = args.<Integer>getOne("seconds").get();
		PLUGIN.setInterval(seconds);
		src.sendMessage(Text.of(TextColors.GREEN, "Announcement interval changed successfully!"));
		return CommandResult.success();
	}
}