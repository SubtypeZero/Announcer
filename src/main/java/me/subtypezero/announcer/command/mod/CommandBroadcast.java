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

public class CommandBroadcast implements CommandExecutor {
	private static final Announcer PLUGIN = Announcer.getInstance();
	public static final Text HELP_TEXT = Text.of("broadcast an announcement");

	private static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.ADMIN_PERM)
			.permission(Permissions.MOD_PERM)
			.permission(Permissions.COMMAND_BROADCAST)
			.description(HELP_TEXT)
			.arguments(GenericArguments.integer(Text.of("index")))
			.executor(new CommandBroadcast())
			.build();

	public static void register() {
		try {
			CommandAnnounce.addSubCommand(commandSpec, "broadcast");
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
			PLUGIN.getLogger().debug("Registered command: CommandBroadcast");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandBroadcast");
		}
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		int index = args.<Integer>getOne("index").get();
		PLUGIN.announce(index);
		src.sendMessage(Text.of(TextColors.GREEN, "Announcement removed successfully!"));
		return CommandResult.success();
	}
}
