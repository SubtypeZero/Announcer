package me.subtypezero.announcer.command.mod;

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
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.ArrayList;
import java.util.List;

public class CommandList implements CommandExecutor {
	private static final Announcer PLUGIN = Announcer.getInstance();
	public static final Text HELP_TEXT = Text.of("view the list of announcements");

	private static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.ADMIN_PERM)
			.permission(Permissions.MOD_PERM)
			.permission(Permissions.COMMAND_LIST)
			.description(HELP_TEXT)
			.executor(new CommandList())
			.build();

	public static void register() {
		try {
			CommandAnnounce.addSubCommand(commandSpec, "list");
			PLUGIN.getGame().getCommandManager().register(PLUGIN, commandSpec);
			PLUGIN.getLogger().debug("Registered command: CommandList");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			PLUGIN.getLogger().error("Failed to register command: CommandList");
		}
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (PLUGIN.getMessages().isEmpty()) {
			src.sendMessage(Text.of(TextColors.GOLD, "There are currently no announcements!"));
			return CommandResult.empty();
		}

		List<Text> text = new ArrayList<>();

		for (int i = 0; i < PLUGIN.getMessages().size(); i++) {
			text.add(Text.of(TextColors.YELLOW, i + 1, TextColors.DARK_GRAY, " - ", TextColors.GRAY, TextSerializers.FORMATTING_CODE.deserialize(PLUGIN.getMessage(i))));
		}

		if (!(src instanceof Player)) {
			text.forEach(src::sendMessage);
		} else {
			PaginationList.builder()
					.title(Text.of(TextColors.RED, PluginInfo.NAME, " List"))
					.padding(Text.of(TextColors.WHITE, TextStyles.STRIKETHROUGH, "-"))
					.contents(text)
					.sendTo(src);
		}

		return CommandResult.success();
	}
}