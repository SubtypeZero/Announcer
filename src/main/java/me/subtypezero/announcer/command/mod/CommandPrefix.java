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

public class CommandPrefix implements CommandExecutor {
	private static final Announcer PLUGIN = Announcer.getInstance();
	public static final Text HELP_TEXT = Text.of("change the announcement prefix");

	private static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.ADMIN_PERM)
			.permission(Permissions.MOD_PERM)
			.permission(Permissions.COMMAND_PREFIX)
			.description(HELP_TEXT)
			.arguments(GenericArguments.string(Text.of("value")))
			.executor(new CommandPrefix())
			.build();

	public static void register() {
		try {
			CommandAnnounce.addSubCommand(commandSpec, "prefix");
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
			PLUGIN.getLogger().debug("Registered command: CommandPrefix");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandPrefix");
		}
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		String prefix = args.<String>getOne("value").get();
		PLUGIN.setPrefix(prefix);
		src.sendMessage(Text.of(TextColors.GREEN, "Announcement prefix changed successfully!"));
		return CommandResult.success();
	}
}
