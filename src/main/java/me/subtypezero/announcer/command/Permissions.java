package me.subtypezero.announcer.command;

public class Permissions {
	// User Permissions
	public static final String MESSAGE_RECEIVER = "announcer.receiver"; // Allows users to receive notifications

	// Mod Permissions
	public static final String MOD_PERM = "announcer.moderate"; // Allows users to use all moderation commands
	public static final String COMMAND_HELP = "announcer.help"; // Allows users to view help information
	public static final String COMMAND_ADD = "announcer.add"; // Allows users to add an announcement
	public static final String COMMAND_REMOVE = "announcer.remove"; // Allows users to remove an announcement
	public static final String COMMAND_BROADCAST = "announcer.broadcast"; // Allows users to broadcast an announcement
	public static final String COMMAND_SAY = "announcer.say"; // Allows users to broadcast a custom message once
	public static final String COMMAND_INTERVAL = "announcer.interval"; // Allows users to change the announcement interval
	public static final String COMMAND_LIST = "announcer.list"; // Allows users to view the list of announcements
	public static final String COMMAND_RANDOM = "announcer.random"; // Allows users to change the announcement mode (random or sequential)
	public static final String COMMAND_PREFIX = "announcer.prefix"; // Allows users to change the announcement prefix

	// Admin Permissions
	public static final String ADMIN_PERM = "announcer.admin"; // Allows users to use all admin commands
	public static final String COMMAND_ENABLE = "announcer.enable"; // Allows users to enable and disable announcements
	public static final String COMMAND_RELOAD = "announcer.reload"; // Allows users to reload the plugin configuration
	public static final String COMMAND_VERSION = "announcer.version"; // Allows users to view plugin version information
}
